package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.common.enums.AuthProvider;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.service.email.EmailService;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.iam.dto.request.ForgotPasswordRequest;
import com.fcs.be.modules.iam.dto.request.LoginRequest;
import com.fcs.be.modules.iam.dto.request.RefreshTokenRequest;
import com.fcs.be.modules.iam.dto.request.RegisterRequest;
import com.fcs.be.modules.iam.dto.request.ResetPasswordRequest;
import com.fcs.be.modules.iam.dto.response.AuthResponse;
import com.fcs.be.modules.iam.entity.AuthIdentity;
import com.fcs.be.modules.iam.entity.RefreshToken;
import com.fcs.be.modules.iam.entity.UserRole;
import com.fcs.be.modules.iam.mapper.AuthMapper;
import com.fcs.be.modules.iam.repository.UserRoleRepository;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.AuthIdentityRepository;
import com.fcs.be.modules.iam.repository.RefreshTokenRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.iam.service.interfaces.AuthService;
import com.fcs.be.modules.iam.service.interfaces.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final WalletRepository walletRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final AuthMapper authMapper;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;

    public AuthServiceImpl(
        UserRepository userRepository,
        AuthIdentityRepository authIdentityRepository,
        RefreshTokenRepository refreshTokenRepository,
        WalletRepository walletRepository,
        JwtTokenService jwtTokenService,
        PasswordEncoder passwordEncoder,
        UserRoleRepository userRoleRepository,
        AuthMapper authMapper,
        StringRedisTemplate redisTemplate,
        EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.authIdentityRepository = authIdentityRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.walletRepository = walletRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.authMapper = authMapper;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameAndIsDeletedFalse(request.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmailAndIsDeletedFalse(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .phone(request.phone())
            .status(UserStatus.ACTIVE)
            .build();
        User savedUser = userRepository.save(user);

        AuthIdentity identity = AuthIdentity.builder()
            .user(savedUser)
            .provider(AuthProvider.LOCAL)
            .providerEmail(request.email())
            .emailVerified(false)
            .passwordHash(passwordEncoder.encode(request.password()))
            .primary(true)
            .build();
        AuthIdentity savedIdentity = authIdentityRepository.save(identity);

        Wallet wallet = Wallet.builder()
            .user(savedUser)
            .balance(BigDecimal.ZERO)
            .availableBalance(BigDecimal.ZERO)
            .build();
        walletRepository.save(wallet);

        return issueTokenPair(savedUser, savedIdentity);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.identifier())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active");
        }

        AuthIdentity identity = authIdentityRepository
            .findByUserIdAndProvider(user.getId(), AuthProvider.LOCAL)
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), identity.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return issueTokenPair(user, identity);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(tokenHash)
            .orElseThrow(() -> new BadCredentialsException("Invalid or expired refresh token"));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            stored.setRevokedAt(Instant.now());
            stored.setRevokeReason("expired");
            refreshTokenRepository.save(stored);
            throw new BadCredentialsException("Refresh token expired");
        }

        try {
            jwtTokenService.parseRefreshToken(request.refreshToken());
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        stored.setRevokedAt(Instant.now());
        stored.setRevokeReason("rotated");
        refreshTokenRepository.save(stored);

        return issueTokenPair(stored.getUser(), stored.getIdentity());
    }

    @Override
    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId, "logout", Instant.now());
    }

    @Transactional
    private AuthResponse issueTokenPair(User user, AuthIdentity identity) {
        List<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
            .map(ur -> ur.getRole().getName())
            .toList();

        Map<String, Object> claims = Map.of(
            "username", user.getUsername(),
            "roles", roles
        );
        String accessToken = jwtTokenService.generateAccessToken(user.getId(), claims);
        String rawRefreshToken = jwtTokenService.generateRefreshToken(user.getId(), claims);

        Claims refreshClaims = jwtTokenService.parseRefreshToken(rawRefreshToken);
        RefreshToken token = RefreshToken.builder()
            .user(user)
            .identity(identity)
            .tokenHash(hashToken(rawRefreshToken))
            .issuedAt(refreshClaims.getIssuedAt().toInstant())
            .expiresAt(refreshClaims.getExpiration().toInstant())
            .build();
        refreshTokenRepository.save(token);

        return authMapper.toResponse(accessToken, rawRefreshToken, user);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(request.email());
        if (userOpt.isEmpty()) {
            return; // Silently return to prevent email enumeration
        }
        User user = userOpt.get();

        String token = UUID.randomUUID().toString();
        String redisKey = "pwd_reset:" + token;

        // Save to Redis with 15 mins TTL
        redisTemplate.opsForValue().set(redisKey, user.getId().toString(), 15, TimeUnit.MINUTES);

        // Generate reset link (assuming frontend is running on localhost:3000, should be in config in a real app)
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String redisKey = "pwd_reset:" + request.token();
        String userIdStr = redisTemplate.opsForValue().get(redisKey);

        if (userIdStr == null) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        // Also update AuthIdentity if present
        authIdentityRepository.findByUserIdAndProvider(userId, AuthProvider.LOCAL)
            .ifPresent(identity -> {
                identity.setPasswordHash(passwordEncoder.encode(request.newPassword()));
                authIdentityRepository.save(identity);
            });

        // Delete token after successful reset
        redisTemplate.delete(redisKey);
    }
}

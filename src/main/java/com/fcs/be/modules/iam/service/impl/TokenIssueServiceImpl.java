package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.response.AuthResponse;
import com.fcs.be.modules.iam.entity.AuthIdentity;
import com.fcs.be.modules.iam.entity.RefreshToken;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.RefreshTokenRepository;
import com.fcs.be.modules.iam.repository.UserRoleRepository;
import com.fcs.be.modules.iam.service.interfaces.JwtTokenService;
import com.fcs.be.modules.iam.service.interfaces.TokenIssueService;
import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenIssueServiceImpl implements TokenIssueService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final UserRoleRepository userRoleRepository;

    public TokenIssueServiceImpl(
        RefreshTokenRepository refreshTokenRepository,
        JwtTokenService jwtTokenService,
        UserRoleRepository userRoleRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenService = jwtTokenService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public AuthResponse issueTokenPair(User user, AuthIdentity identity) {
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

        return new AuthResponse(accessToken, rawRefreshToken, user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), roles);
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
}

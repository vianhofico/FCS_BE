package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.common.enums.AuthProvider;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.iam.dto.response.AuthResponse;
import com.fcs.be.modules.iam.entity.AuthIdentity;
import com.fcs.be.modules.iam.entity.Role;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.entity.UserRole;
import com.fcs.be.modules.iam.repository.AuthIdentityRepository;
import com.fcs.be.modules.iam.repository.RoleRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.iam.repository.UserRoleRepository;
import com.fcs.be.modules.iam.service.interfaces.OAuth2AuthService;
import com.fcs.be.modules.iam.service.interfaces.TokenIssueService;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Locale;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2AuthServiceImpl implements OAuth2AuthService {

    private final UserRepository userRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final WalletRepository walletRepository;
    private final TokenIssueService tokenIssueService;

    public OAuth2AuthServiceImpl(
        UserRepository userRepository,
        AuthIdentityRepository authIdentityRepository,
        RoleRepository roleRepository,
        UserRoleRepository userRoleRepository,
        WalletRepository walletRepository,
        TokenIssueService tokenIssueService
    ) {
        this.userRepository = userRepository;
        this.authIdentityRepository = authIdentityRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.walletRepository = walletRepository;
        this.tokenIssueService = tokenIssueService;
    }

    @Override
    @Transactional
    public AuthResponse authenticateGoogleUser(OAuth2User principal) {
        String providerUserId = requireAttribute(principal, "sub");
        String email = requireAttribute(principal, "email").toLowerCase(Locale.ROOT);
        boolean emailVerified = Boolean.TRUE.equals(principal.getAttribute("email_verified"));

        if (!emailVerified) {
            throw new BadCredentialsException("Google email is not verified");
        }

        AuthIdentity identity = authIdentityRepository
            .findByProviderEmailAndProvider(email, AuthProvider.GOOGLE)
            .orElseGet(() -> createOrLinkGoogleIdentity(principal, providerUserId, email));

        User user = identity.getUser();
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BadCredentialsException("Account is not active");
        }

        String fullName = principal.getAttribute("name");
        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName);
        }
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return tokenIssueService.issueTokenPair(user, identity);
    }

    private AuthIdentity createOrLinkGoogleIdentity(OAuth2User principal, String providerUserId, String email) {
        User user = userRepository.findByUsernameOrEmail(email)
            .orElseGet(() -> createGoogleUser(principal, email));

        AuthIdentity identity = AuthIdentity.builder()
            .user(user)
            .provider(AuthProvider.GOOGLE)
            .providerUserId(providerUserId)
            .providerEmail(email)
            .emailVerified(true)
            .primary(false)
            .build();
        return authIdentityRepository.save(identity);
    }

    private User createGoogleUser(OAuth2User principal, String email) {
        User user = User.builder()
            .username(uniqueUsername(principal, email))
            .email(email)
            .fullName(principal.getAttribute("name"))
            .passwordHash("")
            .status(UserStatus.ACTIVE)
            .build();
        User savedUser = userRepository.save(user);

        Role buyerRole = roleRepository.findByNameAndIsDeletedFalse("BUYER")
            .orElseThrow(() -> new IllegalStateException("BUYER role not found"));
        userRoleRepository.save(UserRole.builder().user(savedUser).role(buyerRole).build());

        Wallet wallet = Wallet.builder()
            .user(savedUser)
            .balance(BigDecimal.ZERO)
            .availableBalance(BigDecimal.ZERO)
            .build();
        walletRepository.save(wallet);

        return savedUser;
    }

    private String uniqueUsername(OAuth2User principal, String email) {
        String source = principal.getAttribute("name");
        if (source == null || source.isBlank()) {
            source = email.substring(0, email.indexOf('@'));
        }

        String base = Normalizer.normalize(source, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9_]", "_")
            .replaceAll("_+", "_")
            .replaceAll("^_|_$", "");

        if (base.length() < 3) {
            base = "google_user";
        }
        if (base.length() > 60) {
            base = base.substring(0, 60);
        }

        String username = base;
        int suffix = 1;
        while (userRepository.existsByUsernameAndIsDeletedFalse(username)) {
            username = base + "_" + suffix++;
        }
        return username;
    }

    private String requireAttribute(OAuth2User principal, String name) {
        String value = principal.getAttribute(name);
        if (value == null || value.isBlank()) {
            throw new BadCredentialsException("Missing Google account attribute: " + name);
        }
        return value;
    }
}

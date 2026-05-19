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
import com.fcs.be.modules.iam.service.interfaces.TokenIssueService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TokenIssueService tokenIssueService;

    private OAuth2AuthServiceImpl oauth2AuthService;

    @BeforeEach
    void setUp() {
        oauth2AuthService = new OAuth2AuthServiceImpl(
            userRepository,
            authIdentityRepository,
            roleRepository,
            userRoleRepository,
            walletRepository,
            tokenIssueService
        );
    }

    @Test
    void authenticateGoogleUserWithExistingGoogleIdentityIssuesTokens() {
        User user = activeUser("buyer", "buyer@example.com");
        AuthIdentity identity = AuthIdentity.builder()
            .user(user)
            .provider(AuthProvider.GOOGLE)
            .providerEmail("buyer@example.com")
            .providerUserId("google-123")
            .emailVerified(true)
            .build();
        AuthResponse expected = authResponse(user, List.of("BUYER"));

        when(authIdentityRepository.findByProviderEmailAndProvider("buyer@example.com", AuthProvider.GOOGLE))
            .thenReturn(Optional.of(identity));
        when(tokenIssueService.issueTokenPair(user, identity)).thenReturn(expected);

        AuthResponse response = oauth2AuthService.authenticateGoogleUser(googleUser("google-123", "buyer@example.com", true));

        assertEquals(expected, response);
        verify(userRepository).save(user);
        verify(tokenIssueService).issueTokenPair(user, identity);
    }

    @Test
    void authenticateGoogleUserLinksExistingLocalUserByEmail() {
        User user = activeUser("buyer", "buyer@example.com");
        AuthResponse expected = authResponse(user, List.of("BUYER"));

        when(authIdentityRepository.findByProviderEmailAndProvider("buyer@example.com", AuthProvider.GOOGLE))
            .thenReturn(Optional.empty());
        when(userRepository.findByUsernameOrEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(authIdentityRepository.save(any(AuthIdentity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenIssueService.issueTokenPair(any(User.class), any(AuthIdentity.class))).thenReturn(expected);

        AuthResponse response = oauth2AuthService.authenticateGoogleUser(googleUser("google-123", "buyer@example.com", true));

        assertEquals(expected, response);
        ArgumentCaptor<AuthIdentity> identityCaptor = ArgumentCaptor.forClass(AuthIdentity.class);
        verify(authIdentityRepository).save(identityCaptor.capture());
        assertEquals(AuthProvider.GOOGLE, identityCaptor.getValue().getProvider());
        assertEquals(user, identityCaptor.getValue().getUser());
    }

    @Test
    void authenticateGoogleUserCreatesBuyerUserAndWallet() {
        Role buyerRole = Role.builder().name("BUYER").build();
        User savedUser = activeUser("new_buyer", "new@example.com");
        AuthResponse expected = authResponse(savedUser, List.of("BUYER"));

        when(authIdentityRepository.findByProviderEmailAndProvider("new@example.com", AuthProvider.GOOGLE))
            .thenReturn(Optional.empty());
        when(userRepository.findByUsernameOrEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.existsByUsernameAndIsDeletedFalse("new_buyer")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(roleRepository.findByNameAndIsDeletedFalse("BUYER")).thenReturn(Optional.of(buyerRole));
        when(authIdentityRepository.save(any(AuthIdentity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenIssueService.issueTokenPair(any(User.class), any(AuthIdentity.class))).thenReturn(expected);

        AuthResponse response = oauth2AuthService.authenticateGoogleUser(googleUser("google-123", "new@example.com", true));

        assertEquals(expected, response);
        verify(userRoleRepository).save(any(UserRole.class));
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void authenticateGoogleUserRejectsUnverifiedEmail() {
        assertThrows(
            BadCredentialsException.class,
            () -> oauth2AuthService.authenticateGoogleUser(googleUser("google-123", "buyer@example.com", false))
        );
        verify(authIdentityRepository, never()).save(any());
    }

    private OAuth2User googleUser(String sub, String email, boolean verified) {
        return new DefaultOAuth2User(
            List.of(),
            Map.of("sub", sub, "email", email, "email_verified", verified, "name", "New Buyer"),
            "sub"
        );
    }

    private User activeUser(String username, String email) {
        return User.builder()
            .id(UUID.randomUUID())
            .username(username)
            .email(email)
            .passwordHash("")
            .status(UserStatus.ACTIVE)
            .build();
    }

    private AuthResponse authResponse(User user, List<String> roles) {
        return new AuthResponse("access-token", "refresh-token", user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}

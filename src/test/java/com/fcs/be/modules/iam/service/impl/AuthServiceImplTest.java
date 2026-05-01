package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.iam.dto.request.LoginRequest;
import com.fcs.be.modules.iam.dto.request.RegisterRequest;
import com.fcs.be.modules.iam.dto.response.AuthResponse;
import com.fcs.be.modules.iam.entity.AuthIdentity;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.AuthIdentityRepository;
import com.fcs.be.modules.iam.repository.RefreshTokenRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthServiceImplTest {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthIdentityRepository authIdentityRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        authIdentityRepository.deleteAll();
        userRepository.deleteAll();
        walletRepository.deleteAll();
        
        validRegisterRequest = new RegisterRequest(
            "testuser",
            "test@example.com",
            "Password123!",
            "0901234567"
        );
    }

    @Test
    void testRegisterSuccess() {
        AuthResponse response = authService.register(validRegisterRequest);

        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertNotNull(response.userId());
        assertEquals("testuser", response.username());
        assertEquals("test@example.com", response.email());

        User savedUser = userRepository.findByIdAndIsDeletedFalse(response.userId()).orElse(null);
        assertNotNull(savedUser);
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());

        Wallet wallet = walletRepository.findByUserIdAndIsDeletedFalse(response.userId()).orElse(null);
        assertNotNull(wallet);
    }

    @Test
    void testRegisterWithDuplicateUsername() {
        authService.register(validRegisterRequest);

        RegisterRequest duplicateRequest = new RegisterRequest(
            "testuser",
            "another@example.com",
            "Password123!",
            "0901234567"
        );

        assertThrows(IllegalArgumentException.class, () -> authService.register(duplicateRequest));
    }

    @Test
    void testRegisterWithDuplicateEmail() {
        authService.register(validRegisterRequest);

        RegisterRequest duplicateRequest = new RegisterRequest(
            "anotheruser",
            "test@example.com",
            "Password123!",
            "0901234567"
        );

        assertThrows(IllegalArgumentException.class, () -> authService.register(duplicateRequest));
    }

    @Test
    void testLoginSuccess() {
        authService.register(validRegisterRequest);

        LoginRequest loginRequest = new LoginRequest("testuser", "Password123!");
        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("testuser", response.username());
    }

    @Test
    void testLoginWithInvalidUsername() {
        LoginRequest loginRequest = new LoginRequest("nonexistent", "Password123!");

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLoginWithInvalidPassword() {
        authService.register(validRegisterRequest);

        LoginRequest loginRequest = new LoginRequest("testuser", "WrongPassword123!");

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }

    @Test
    void testLoginWithEmail() {
        authService.register(validRegisterRequest);

        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");
        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("testuser", response.username());
    }

    @Test
    void testLogout() {
        AuthResponse authResponse = authService.register(validRegisterRequest);

        authService.logout(authResponse.userId());

        // Verify all refresh tokens for user are revoked
        long activeTokens = refreshTokenRepository.findAll().stream()
            .filter(rt -> rt.getUser().getId().equals(authResponse.userId()) && rt.getRevokedAt() == null)
            .count();
        assertEquals(0, activeTokens);
    }
}

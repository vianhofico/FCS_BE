package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.iam.dto.request.ChangePasswordRequest;
import com.fcs.be.modules.iam.dto.request.UpdateUserProfileRequest;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .username("testuser")
            .email("testuser@example.com")
            .passwordHash(passwordEncoder.encode("oldPassword123!"))
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(testUser);
    }

    @Test
    void testUpdateProfileSuccess() {
        UpdateUserProfileRequest request = new UpdateUserProfileRequest("newusername", "0123456789");
        UserSummaryResponse response = userService.updateProfile(testUser.getId(), request);

        assertNotNull(response);
        assertEquals("newusername", response.username());
        assertEquals("0123456789", response.phone());

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals("newusername", updatedUser.getUsername());
        assertEquals("0123456789", updatedUser.getPhone());
    }

    @Test
    void testUpdateProfileDuplicateUsername() {
        User anotherUser = User.builder()
            .username("existinguser")
            .email("existing@example.com")
            .passwordHash("hash")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(anotherUser);

        UpdateUserProfileRequest request = new UpdateUserProfileRequest("existinguser", "0123456789");
        assertThrows(IllegalArgumentException.class, () -> userService.updateProfile(testUser.getId(), request));
    }

    @Test
    void testChangePasswordSuccess() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword123!", "newPassword123!", "newPassword123!");

        assertDoesNotThrow(() -> userService.changePassword(testUser.getId(), request));

        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword123!", updatedUser.getPasswordHash()));
    }

    @Test
    void testChangePasswordIncorrectCurrentPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword!", "newPassword123!", "newPassword123!");

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(testUser.getId(), request));
    }

    @Test
    void testChangePasswordMismatchNewPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword123!", "newPassword123!", "differentPassword!");

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(testUser.getId(), request));
    }
}

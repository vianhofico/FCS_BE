package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.dto.request.ChangePasswordRequest;
import com.fcs.be.modules.iam.dto.request.UpdateUserProfileRequest;
import com.fcs.be.modules.iam.dto.request.UserFilterRequest;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.entity.Role;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.entity.UserRole;
import com.fcs.be.modules.iam.mapper.UserMapper;
import com.fcs.be.modules.iam.repository.RoleRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.iam.repository.UserRoleRepository;
import com.fcs.be.modules.iam.repository.UserSpecification;
import com.fcs.be.modules.iam.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        RoleRepository roleRepository,
        UserMapper userMapper,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResponse<UserSummaryResponse> getUsers(UserFilterRequest filter, Pageable pageable) {
        return PageResponse.of(
            userRepository.findAll(UserSpecification.from(filter), pageable)
                .map(userMapper::toSummary)
        );
    }

    @Override
    public UserSummaryResponse getUser(UUID id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
            .map(userMapper::toSummary)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public UserSummaryResponse updateProfile(UUID id, UpdateUserProfileRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!user.getUsername().equals(request.username()) &&
            userRepository.existsByUsernameAndIsDeletedFalse(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setUsername(request.username());
        user.setPhone(request.phone());
        return userMapper.toSummary(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        User user = userRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserSummaryResponse updateStatus(UUID id, UserStatus status) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setStatus(status);
        return userMapper.toSummary(userRepository.save(user));
    }

    @Override
    @Transactional
    public void assignRoles(UUID userId, List<UUID> roleIds) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (UUID roleId : roleIds) {
            if (userRoleRepository.findByUserIdAndRoleId(userId, roleId).isEmpty()) {
                Role role = roleRepository.findByIdAndIsDeletedFalse(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));
                UserRole userRole = new UserRole();
                userRole.setUser(user);
                userRole.setRole(role);
                userRoleRepository.save(userRole);
            }
        }
    }

    @Override
    @Transactional
    public void removeRole(UUID userId, UUID roleId) {
        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
            .orElseThrow(() -> new EntityNotFoundException("User role mapping not found"));
        userRoleRepository.delete(userRole);
    }
}

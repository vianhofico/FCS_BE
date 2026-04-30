package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.mapper.UserMapper;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.iam.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserSummaryResponse getUser(UUID id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
            .map(userMapper::toSummary)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
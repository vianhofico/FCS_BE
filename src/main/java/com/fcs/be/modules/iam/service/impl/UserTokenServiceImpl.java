package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.response.UserTokenPreviewResponse;
import com.fcs.be.modules.iam.mapper.UserTokenPreviewMapper;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.iam.service.interfaces.JwtTokenService;
import com.fcs.be.modules.iam.service.interfaces.UserTokenService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final UserTokenPreviewMapper userTokenPreviewMapper;

    public UserTokenServiceImpl(
        UserRepository userRepository,
        JwtTokenService jwtTokenService,
        UserTokenPreviewMapper userTokenPreviewMapper
    ) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
        this.userTokenPreviewMapper = userTokenPreviewMapper;
    }

    @Override
    public UserTokenPreviewResponse previewTokens(UUID userId) {
        userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String accessToken = jwtTokenService.generateAccessToken(userId, Map.of("scope", "preview"));
        String refreshToken = jwtTokenService.generateRefreshToken(userId, Map.of("scope", "preview"));
        return userTokenPreviewMapper.toResponse(accessToken, refreshToken);
    }
}
package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.modules.iam.dto.response.TokenPreviewResponse;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.mapper.IamMapper;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.iam.service.interfaces.IamService;
import com.fcs.be.modules.iam.service.interfaces.JwtTokenService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class IamServiceImpl implements IamService {

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final IamMapper iamMapper;

    public IamServiceImpl(UserRepository userRepository, JwtTokenService jwtTokenService, IamMapper iamMapper) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
        this.iamMapper = iamMapper;
    }

    @Override
    public UserSummaryResponse getUser(UUID id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
            .map(iamMapper::toSummary)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public TokenPreviewResponse previewTokens(UUID userId) {
        userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String accessToken = jwtTokenService.generateAccessToken(userId, Map.of("scope", "preview"));
        String refreshToken = jwtTokenService.generateRefreshToken(userId, Map.of("scope", "preview"));
        return new TokenPreviewResponse(accessToken, refreshToken);
    }
}

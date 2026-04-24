package com.fcs.be.modules.iam.service.interfaces;

import io.jsonwebtoken.Claims;
import java.util.Map;
import java.util.UUID;

public interface JwtTokenService {

    String generateAccessToken(UUID userId, Map<String, Object> claims);

    String generateRefreshToken(UUID userId, Map<String, Object> claims);

    Claims parseAccessToken(String token);

    Claims parseRefreshToken(String token);
}

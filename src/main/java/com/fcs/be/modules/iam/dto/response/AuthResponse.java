package com.fcs.be.modules.iam.dto.response;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    UUID userId,
    String username,
    String email,
    String fullName,
    List<String> roles
) {
    public AuthResponse(String accessToken, String refreshToken, UUID userId, String username, String email, List<String> roles) {
        this(accessToken, refreshToken, userId, username, email, null, roles);
    }
}

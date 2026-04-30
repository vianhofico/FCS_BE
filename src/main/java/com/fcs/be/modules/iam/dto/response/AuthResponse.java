package com.fcs.be.modules.iam.dto.response;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    UUID userId,
    String username,
    String email
) {}

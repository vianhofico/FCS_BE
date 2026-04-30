package com.fcs.be.modules.iam.dto.response;

public record UserTokenPreviewResponse(String accessToken, String refreshToken) {
}
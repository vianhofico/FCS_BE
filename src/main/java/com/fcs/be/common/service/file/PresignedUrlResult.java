package com.fcs.be.common.service.file;

import java.time.Instant;

public record PresignedUrlResult(
    String objectKey,
    String url,
    Instant expiresAt
) {
}

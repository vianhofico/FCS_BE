package com.fcs.be.modules.audit.dto.response;

import com.fcs.be.common.enums.ActivityAction;
import java.time.Instant;
import java.util.UUID;

public record ActivityLogResponse(
    UUID id,
    UUID userId,
    ActivityAction action,
    String entityName,
    UUID entityId,
    String oldValues,
    String newValues,
    String ipAddress,
    String userAgent,
    Instant createdAt
) {
}

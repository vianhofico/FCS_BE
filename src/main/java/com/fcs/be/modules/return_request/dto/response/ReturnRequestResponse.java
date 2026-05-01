package com.fcs.be.modules.return_request.dto.response;

import com.fcs.be.common.enums.ReturnRequestStatus;
import java.time.Instant;
import java.util.UUID;

public record ReturnRequestResponse(
    UUID id,
    UUID orderId,
    UUID requestedById,
    String reason,
    String evidenceUrls,
    ReturnRequestStatus status,
    UUID reviewedById,
    String reviewNote,
    Instant reviewedAt,
    Instant createdAt
) {}

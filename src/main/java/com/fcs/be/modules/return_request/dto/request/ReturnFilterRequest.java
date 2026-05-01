package com.fcs.be.modules.return_request.dto.request;

import com.fcs.be.common.enums.ReturnRequestStatus;
import java.time.Instant;
import java.util.UUID;

public record ReturnFilterRequest(
    UUID orderId,
    UUID requestedById,
    ReturnRequestStatus status,
    Instant startDate,
    Instant endDate
) {}

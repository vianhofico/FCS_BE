package com.fcs.be.modules.consignment.dto.response;

import com.fcs.be.common.enums.ConsignmentItemStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record ConsignmentItemResponse(
    UUID id,
    UUID requestId,
    String suggestedName,
    BigDecimal suggestedPrice,
    String conditionNote,
    ConsignmentItemStatus status,
    String rejectionReason
) {}

package com.fcs.be.modules.consignment.dto.response;

import com.fcs.be.common.enums.ConsignmentItemStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record ConsignmentItemResponse(
    UUID id,
    UUID requestId,
    String suggestedName,
    BigDecimal suggestedPrice,
    BigDecimal originalPrice,
    UUID suggestedBrandId,
    UUID suggestedCategoryId,
    String conditionNote,
    ConsignmentItemStatus status,
    String rejectionReason
) {}

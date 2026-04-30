package com.fcs.be.modules.consignment.dto.response;

import com.fcs.be.common.enums.ConsignmentContractStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ConsignmentContractResponse(
    UUID id,
    UUID requestId,
    BigDecimal commissionRate,
    BigDecimal agreedPrice,
    Instant signedAt,
    Instant validUntil,
    ConsignmentContractStatus status
) {}

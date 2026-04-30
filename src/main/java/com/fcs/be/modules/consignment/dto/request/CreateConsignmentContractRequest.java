package com.fcs.be.modules.consignment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateConsignmentContractRequest(
    @NotNull UUID requestId,
    @NotNull @DecimalMin("0.0") BigDecimal commissionRate,
    @NotNull @DecimalMin("0.0") BigDecimal agreedPrice,
    Instant validUntil
) {}

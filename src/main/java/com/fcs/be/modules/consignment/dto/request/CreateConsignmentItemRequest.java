package com.fcs.be.modules.consignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateConsignmentItemRequest(
    @NotNull UUID requestId,
    @NotBlank String suggestedName,
    BigDecimal suggestedPrice,
    String conditionNote
) {}

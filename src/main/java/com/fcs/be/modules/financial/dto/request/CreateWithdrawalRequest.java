package com.fcs.be.modules.financial.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateWithdrawalRequest(
    @NotNull UUID walletId,
    @NotNull @DecimalMin("10000.0") BigDecimal amount
) {}

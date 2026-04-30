package com.fcs.be.modules.financial.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateWalletRequest(
    @NotNull @DecimalMin("0.00") BigDecimal availableBalance,
    @Size(max = 120) String bankName,
    @Size(max = 150) String bankAccountName,
    @Size(max = 60) String bankAccountNumber
) {
}

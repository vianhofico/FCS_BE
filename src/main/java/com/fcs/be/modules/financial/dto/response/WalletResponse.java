package com.fcs.be.modules.financial.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponse(
    UUID id,
    UUID userId,
    BigDecimal balance,
    BigDecimal availableBalance,
    String bankName,
    String bankAccountName,
    String bankAccountNumber
) {
}

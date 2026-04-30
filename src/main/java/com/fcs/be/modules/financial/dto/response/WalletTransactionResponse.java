package com.fcs.be.modules.financial.dto.response;

import com.fcs.be.common.enums.WalletTransactionStatus;
import com.fcs.be.common.enums.WalletTransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletTransactionResponse(
    UUID id,
    UUID walletId,
    UUID orderId,
    BigDecimal amount,
    WalletTransactionType type,
    String referenceType,
    UUID referenceId,
    String description,
    WalletTransactionStatus status,
    String idempotencyKey,
    Instant createdAt
) {}

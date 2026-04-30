package com.fcs.be.modules.financial.dto.response;

import com.fcs.be.common.enums.WithdrawalStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WithdrawalRequestResponse(
    UUID id,
    String requestCode,
    UUID walletId,
    BigDecimal amount,
    WithdrawalStatus status,
    UUID reviewedBy,
    Instant reviewedAt,
    String rejectReason,
    String bankSnapshotName,
    String bankSnapshotNumber,
    String bankSnapshotBranch,
    String transferReference,
    String receiptImageUrl,
    Instant transferredAt,
    Instant createdAt
) {}

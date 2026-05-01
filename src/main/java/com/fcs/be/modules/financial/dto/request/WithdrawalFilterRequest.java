package com.fcs.be.modules.financial.dto.request;

import com.fcs.be.common.enums.WithdrawalStatus;
import java.time.Instant;
import java.util.UUID;

public record WithdrawalFilterRequest(
    UUID walletId,
    WithdrawalStatus status,
    Instant startDate,
    Instant endDate
) {}

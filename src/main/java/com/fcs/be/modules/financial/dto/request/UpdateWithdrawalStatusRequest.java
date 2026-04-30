package com.fcs.be.modules.financial.dto.request;

import com.fcs.be.common.enums.WithdrawalStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateWithdrawalStatusRequest(
    @NotNull WithdrawalStatus status,
    String rejectReason,
    String transferReference,
    String receiptImageUrl
) {}

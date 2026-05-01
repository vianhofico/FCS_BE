package com.fcs.be.modules.order.dto.request;

import com.fcs.be.common.enums.VoucherStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateVoucherStatusRequest(
    @NotNull(message = "Voucher status is required")
    VoucherStatus status
) {}

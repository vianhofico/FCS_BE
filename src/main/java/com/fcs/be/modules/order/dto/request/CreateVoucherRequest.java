package com.fcs.be.modules.order.dto.request;

import com.fcs.be.common.enums.VoucherDiscountType;
import com.fcs.be.common.enums.VoucherStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateVoucherRequest(
    @NotBlank String code,
    @NotNull VoucherDiscountType discountType,
    @NotNull @DecimalMin("0.0") BigDecimal value,
    BigDecimal minOrderValue,
    BigDecimal maxDiscount,
    Instant startDate,
    Instant endDate,
    Integer usageLimit,
    @NotNull VoucherStatus status
) {}

package com.fcs.be.modules.order.dto.response;

import com.fcs.be.common.enums.VoucherDiscountType;
import com.fcs.be.common.enums.VoucherStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VoucherResponse(
    UUID id,
    String code,
    VoucherDiscountType discountType,
    BigDecimal value,
    BigDecimal minOrderValue,
    BigDecimal maxDiscount,
    Instant startDate,
    Instant endDate,
    Integer usageLimit,
    Integer usedCount,
    VoucherStatus status
) {}

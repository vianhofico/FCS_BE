package com.fcs.be.modules.order.entity;

import com.fcs.be.common.entity.SoftDeleteEntity;
import com.fcs.be.common.enums.VoucherDiscountType;
import com.fcs.be.common.enums.VoucherStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vouchers")
public class Voucher extends SoftDeleteEntity {

    @Column(name = "code", nullable = false, unique = true, length = 80)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private VoucherDiscountType discountType;

    @Column(name = "value", nullable = false, precision = 19, scale = 4)
    private BigDecimal value;

    @Column(name = "min_order_value", precision = 19, scale = 4)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount", precision = 19, scale = 4)
    private BigDecimal maxDiscount;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VoucherStatus status;
}

package com.fcs.be.modules.financial.entity;

import com.fcs.be.common.entity.ImmutableLogEntity;
import com.fcs.be.common.enums.WalletTransactionStatus;
import com.fcs.be.common.enums.WalletTransactionType;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.order.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction extends ImmutableLogEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private WalletTransactionType type;

    @Column(name = "reference_type", length = 60)
    private String referenceType;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "reference_id", length = 36)
    private UUID referenceId;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private WalletTransactionStatus status;

    @Column(name = "idempotency_key", unique = true, length = 120)
    private String idempotencyKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}

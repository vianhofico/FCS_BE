package com.fcs.be.modules.payment.entity;

import com.fcs.be.common.entity.BaseEntity;
import com.fcs.be.modules.order.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
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
@Table(name = "payment_transactions")
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "provider", nullable = false, length = 30)
    private String provider;

    @Column(name = "provider_order_code", length = 80)
    private String providerOrderCode;

    @Column(name = "payment_link_id", length = 120)
    private String paymentLinkId;

    @Column(name = "reference", length = 120)
    private String reference;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "status_code", length = 30)
    private String statusCode;

    @Column(name = "status_description", length = 255)
    private String statusDescription;

    @Column(name = "account_number", length = 80)
    private String accountNumber;

    @Column(name = "counter_account_name", length = 180)
    private String counterAccountName;

    @Column(name = "counter_account_number", length = 80)
    private String counterAccountNumber;

    @Column(name = "transaction_date_time")
    private Instant transactionDateTime;

    @Column(name = "signature", length = 255)
    private String signature;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;
}

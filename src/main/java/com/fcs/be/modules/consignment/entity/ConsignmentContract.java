package com.fcs.be.modules.consignment.entity;

import com.fcs.be.common.entity.SoftDeleteEntity;
import com.fcs.be.common.enums.ConsignmentContractStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
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
@Table(name = "consignment_contracts")
public class ConsignmentContract extends SoftDeleteEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private ConsignmentRequest request;

    @Column(name = "commission_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal commissionRate;

    @Column(name = "agreed_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal agreedPrice;

    @Column(name = "signed_at")
    private Instant signedAt;

    @Column(name = "signed_by_user_id", length = 36)
    private UUID signedByUserId;

    @Column(name = "signed_by_name", length = 180)
    private String signedByName;

    @Column(name = "signature_method", length = 50)
    private String signatureMethod;

    @Column(name = "signature_ip_address", length = 80)
    private String signatureIpAddress;

    @Column(name = "signature_user_agent", length = 500)
    private String signatureUserAgent;

    @Column(name = "signature_hash", length = 128)
    private String signatureHash;

    @Column(name = "valid_until")
    private Instant validUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ConsignmentContractStatus status;
}

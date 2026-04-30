package com.fcs.be.modules.consignment.entity;

import com.fcs.be.common.entity.SoftDeleteEntity;
import com.fcs.be.common.enums.ConsignmentItemStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "consignment_items")
public class ConsignmentItem extends SoftDeleteEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private ConsignmentRequest request;

    @Column(name = "suggested_name", nullable = false, length = 255)
    private String suggestedName;

    @Column(name = "suggested_price", precision = 19, scale = 4)
    private BigDecimal suggestedPrice;

    @Column(name = "condition_note", length = 1000)
    private String conditionNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ConsignmentItemStatus status;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;
}

package com.fcs.be.modules.financial.entity;

import com.fcs.be.common.entity.ImmutableLogEntity;
import com.fcs.be.modules.iam.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "withdrawal_status_history")
public class WithdrawalStatusHistory extends ImmutableLogEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "withdrawal_request_id", nullable = false)
    private WithdrawalRequest withdrawalRequest;

    @Column(name = "from_status", length = 40)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 40)
    private String toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Column(name = "reason", length = 1000)
    private String reason;
}

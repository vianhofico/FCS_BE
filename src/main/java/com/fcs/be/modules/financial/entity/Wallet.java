package com.fcs.be.modules.financial.entity;

import com.fcs.be.common.entity.SoftDeleteEntity;
import com.fcs.be.modules.iam.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "wallets")
public class Wallet extends SoftDeleteEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;

    @Column(name = "bank_name", length = 120)
    private String bankName;

    @Column(name = "bank_account_name", length = 150)
    private String bankAccountName;

    @Column(name = "bank_account_number", length = 60)
    private String bankAccountNumber;
}

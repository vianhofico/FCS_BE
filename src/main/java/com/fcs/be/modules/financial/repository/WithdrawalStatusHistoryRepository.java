package com.fcs.be.modules.financial.repository;

import com.fcs.be.modules.financial.entity.WithdrawalStatusHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalStatusHistoryRepository extends JpaRepository<WithdrawalStatusHistory, UUID> {

    List<WithdrawalStatusHistory> findByWithdrawalRequestIdOrderByCreatedAtDesc(UUID withdrawalRequestId);
}

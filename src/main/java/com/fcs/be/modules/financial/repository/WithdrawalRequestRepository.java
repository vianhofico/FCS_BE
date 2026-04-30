package com.fcs.be.modules.financial.repository;

import com.fcs.be.common.enums.WithdrawalStatus;
import com.fcs.be.modules.financial.entity.WithdrawalRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID> {

    Optional<WithdrawalRequest> findByIdAndIsDeletedFalse(UUID id);

    List<WithdrawalRequest> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<WithdrawalRequest> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(WithdrawalStatus status);
}

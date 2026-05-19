package com.fcs.be.modules.financial.repository;

import com.fcs.be.common.enums.WithdrawalStatus;
import com.fcs.be.modules.financial.entity.WithdrawalRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, UUID>, JpaSpecificationExecutor<WithdrawalRequest> {

    Optional<WithdrawalRequest> findByIdAndIsDeletedFalse(UUID id);

    List<WithdrawalRequest> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<WithdrawalRequest> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(WithdrawalStatus status);

    @Query("""
        select coalesce(sum(w.amount), 0)
        from WithdrawalRequest w
        where w.isDeleted = false
          and w.status = :status
          and w.wallet.user.id = :userId
        """)
    BigDecimal sumByUserIdAndStatus(
        @Param("userId") UUID userId,
        @Param("status") WithdrawalStatus status
    );
}

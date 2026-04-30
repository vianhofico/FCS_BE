package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.order.entity.VoucherUsage;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, UUID> {

    Optional<VoucherUsage> findByVoucherIdAndUserIdAndIsDeletedFalse(UUID voucherId, UUID userId);

    long countByVoucherIdAndIsDeletedFalse(UUID voucherId);
}

package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.order.entity.Voucher;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {

    Optional<Voucher> findByCodeAndIsDeletedFalse(String code);

    Optional<Voucher> findByIdAndIsDeletedFalse(UUID id);
}

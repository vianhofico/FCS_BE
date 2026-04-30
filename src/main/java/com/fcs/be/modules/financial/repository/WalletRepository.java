package com.fcs.be.modules.financial.repository;

import com.fcs.be.modules.financial.entity.Wallet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    List<Wallet> findByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<Wallet> findByIdAndIsDeletedFalse(UUID id);
}

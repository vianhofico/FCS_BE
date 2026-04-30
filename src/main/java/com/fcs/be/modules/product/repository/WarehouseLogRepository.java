package com.fcs.be.modules.product.repository;

import com.fcs.be.modules.product.entity.WarehouseLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseLogRepository extends JpaRepository<WarehouseLog, UUID> {

    List<WarehouseLog> findByProductIdOrderByCreatedAtDesc(UUID productId);
}

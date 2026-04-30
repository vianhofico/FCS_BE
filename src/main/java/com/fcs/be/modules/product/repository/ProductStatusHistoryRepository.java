package com.fcs.be.modules.product.repository;

import com.fcs.be.modules.product.entity.ProductStatusHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatusHistoryRepository extends JpaRepository<ProductStatusHistory, UUID> {
}

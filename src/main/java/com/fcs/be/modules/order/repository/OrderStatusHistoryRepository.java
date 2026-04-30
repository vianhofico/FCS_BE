package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.order.entity.OrderStatusHistory;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {
}

package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.order.entity.OrderItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderIdAndIsDeletedFalse(UUID orderId);

    boolean existsByOrderIdAndProductIdAndIsDeletedFalse(UUID orderId, UUID productId);
}

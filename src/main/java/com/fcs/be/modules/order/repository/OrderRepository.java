package com.fcs.be.modules.order.repository;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.modules.order.entity.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    List<Order> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<Order> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(OrderStatus status);

    Optional<Order> findByIdAndIsDeletedFalse(UUID id);
}

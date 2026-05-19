package com.fcs.be.modules.order.repository;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.modules.order.entity.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    List<Order> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<Order> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(OrderStatus status);

    Optional<Order> findByIdAndIsDeletedFalse(UUID id);

    Optional<Order> findByOrderCodeAndIsDeletedFalse(String orderCode);

    @Query("""
        select coalesce(sum(o.totalAmount), 0)
        from Order o
        where o.isDeleted = false
          and o.status in :statuses
        """)
    BigDecimal sumRevenueByStatuses(@Param("statuses") List<OrderStatus> statuses);

    @Query("""
        select coalesce(sum(o.totalAmount), 0)
        from Order o
        where o.isDeleted = false
          and o.status in :statuses
          and o.createdAt >= :start
          and o.createdAt < :end
        """)
    BigDecimal sumRevenueByStatusesAndCreatedAtBetween(
        @Param("statuses") List<OrderStatus> statuses,
        @Param("start") Instant start,
        @Param("end") Instant end
    );

    @Query("""
        select count(o)
        from Order o
        where o.isDeleted = false
          and o.status in :statuses
          and o.createdAt >= :start
          and o.createdAt < :end
        """)
    Long countByStatusesAndCreatedAtBetween(
        @Param("statuses") List<OrderStatus> statuses,
        @Param("start") Instant start,
        @Param("end") Instant end
    );
}

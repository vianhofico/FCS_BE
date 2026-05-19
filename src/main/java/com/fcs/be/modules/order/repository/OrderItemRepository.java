package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.analytics.dto.response.TopProductResponse;
import com.fcs.be.modules.order.entity.OrderItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderIdAndIsDeletedFalse(UUID orderId);

    boolean existsByOrderIdAndProductIdAndIsDeletedFalse(UUID orderId, UUID productId);

    @Query("""
        select new com.fcs.be.modules.analytics.dto.response.TopProductResponse(
            oi.product.id,
            oi.product.sku,
            oi.product.name,
            count(oi),
            coalesce(sum(oi.priceAtPurchase), 0)
        )
        from OrderItem oi
        where oi.isDeleted = false
          and oi.order.isDeleted = false
          and oi.order.status in :statuses
        group by oi.product.id, oi.product.sku, oi.product.name
        order by count(oi) desc, coalesce(sum(oi.priceAtPurchase), 0) desc
        limit 5
        """)
    List<TopProductResponse> findTopProductsByOrderStatuses(@Param("statuses") List<com.fcs.be.common.enums.OrderStatus> statuses);

    @Query("""
        select coalesce(sum(oi.priceAtPurchase), 0)
        from OrderItem oi
        where oi.isDeleted = false
          and oi.order.isDeleted = false
          and oi.order.status in :statuses
          and oi.product.consignmentItem.request.consignor.id = :sellerId
        """)
    BigDecimal sumSellerRevenue(
        @Param("sellerId") UUID sellerId,
        @Param("statuses") List<com.fcs.be.common.enums.OrderStatus> statuses
    );

    @Query("""
        select count(oi)
        from OrderItem oi
        where oi.isDeleted = false
          and oi.order.isDeleted = false
          and oi.order.status in :statuses
          and oi.product.consignmentItem.request.consignor.id = :sellerId
        """)
    Long countSellerSoldItems(
        @Param("sellerId") UUID sellerId,
        @Param("statuses") List<com.fcs.be.common.enums.OrderStatus> statuses
    );
}

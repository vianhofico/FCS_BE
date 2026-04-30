package com.fcs.be.modules.order.mapper;

import com.fcs.be.modules.order.dto.response.OrderItemResponse;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order, List<OrderItem> items) {
        return new OrderResponse(
            order.getId(),
            order.getBuyer().getId(),
            order.getOrderCode(),
            order.getSubTotal(),
            order.getShippingFee(),
            order.getDiscountAmount(),
            order.getTotalAmount(),
            order.getPaymentMethod(),
            order.getShippingAddress() == null ? null : order.getShippingAddress().getId(),
            order.getShippingSnapshot(),
            order.getStatus(),
            items.stream().map(this::toItemResponse).toList()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
            item.getId(),
            item.getProduct().getId(),
            item.getSkuSnapshot(),
            item.getProductNameSnapshot(),
            item.getConditionSnapshot(),
            item.getPriceAtPurchase()
        );
    }
}

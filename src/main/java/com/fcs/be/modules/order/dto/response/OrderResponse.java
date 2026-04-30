package com.fcs.be.modules.order.dto.response;

import com.fcs.be.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    UUID buyerId,
    String orderCode,
    BigDecimal subTotal,
    BigDecimal shippingFee,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    String paymentMethod,
    UUID shippingAddressId,
    String shippingSnapshot,
    OrderStatus status,
    List<OrderItemResponse> items
) {
}

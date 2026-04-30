package com.fcs.be.modules.order.service.interfaces;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.modules.order.dto.request.CreateOrderRequest;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderResponse> getOrders(OrderStatus status);

    OrderResponse getOrder(UUID id);

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse updateStatus(UUID id, OrderStatus status, String reason);

    void deleteOrder(UUID id);
}

package com.fcs.be.modules.order.service.interfaces;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.order.dto.request.CreateOrderRequest;
import com.fcs.be.modules.order.dto.request.OrderFilterRequest;
import com.fcs.be.modules.order.dto.request.UpdateOrderTrackingRequest;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    PageResponse<OrderResponse> getOrders(OrderFilterRequest filter, Pageable pageable);

    OrderResponse getOrder(UUID id);

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse updateStatus(UUID id, OrderStatus status, String reason);

    OrderResponse updateTracking(UUID id, UpdateOrderTrackingRequest request);

    void deleteOrder(UUID id);
}

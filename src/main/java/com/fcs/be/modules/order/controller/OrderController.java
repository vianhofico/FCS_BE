package com.fcs.be.modules.order.controller;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.order.dto.request.CreateOrderRequest;
import com.fcs.be.modules.order.dto.request.OrderFilterRequest;
import com.fcs.be.modules.order.dto.request.UpdateOrderStatusRequest;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import com.fcs.be.modules.order.service.interfaces.OrderService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrders(
        @RequestParam(required = false) String orderCode,
        @RequestParam(required = false) UUID buyerId,
        @RequestParam(required = false) OrderStatus status,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        OrderFilterRequest filter = new OrderFilterRequest(orderCode, buyerId, status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok("Fetched orders", orderService.getOrders(filter, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched order", orderService.getOrder(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Order created", orderService.createOrder(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Order status updated",
            orderService.updateStatus(id, request.status(), request.reason())));
    }

    @PatchMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<OrderResponse>> updateTracking(
        @PathVariable UUID id,
        @Valid @RequestBody com.fcs.be.modules.order.dto.request.UpdateOrderTrackingRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Order tracking updated",
            orderService.updateTracking(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.ok("Order deleted"));
    }
}


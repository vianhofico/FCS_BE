package com.fcs.be.modules.order.controller;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.order.dto.request.CreateOrderRequest;
import com.fcs.be.modules.order.dto.request.UpdateOrderStatusRequest;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import com.fcs.be.modules.order.service.interfaces.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
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
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(@RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched orders", orderService.getOrders(status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched order", orderService.getOrder(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Order created", orderService.createOrder(request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                "Order status updated",
                orderService.updateStatus(id, request.status(), request.reason())
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable UUID id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(ApiResponse.ok("Order deleted"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}

package com.fcs.be.modules.order.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.order.dto.request.AddCartItemRequest;
import com.fcs.be.modules.order.dto.response.CartResponse;
import com.fcs.be.modules.order.service.interfaces.CartService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched cart", cartService.getCart(userId)));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
        @PathVariable UUID userId,
        @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Added item to cart", cartService.addItem(userId, request)));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
        @PathVariable UUID userId,
        @PathVariable UUID itemId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Removed item from cart", cartService.removeItem(userId, itemId)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared"));
    }
}

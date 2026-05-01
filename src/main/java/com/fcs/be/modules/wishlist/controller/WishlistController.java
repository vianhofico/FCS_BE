package com.fcs.be.modules.wishlist.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.wishlist.dto.response.WishlistItemResponse;
import com.fcs.be.modules.wishlist.service.interfaces.WishlistService;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<WishlistItemResponse>>> getWishlist(
        @AuthenticationPrincipal UUID userId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID finalUserId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // fallback for dev
        return ResponseEntity.ok(ApiResponse.ok("Fetched wishlist", wishlistService.getWishlist(finalUserId, pageable)));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> addToWishlist(
        @PathVariable UUID productId,
        @AuthenticationPrincipal UUID userId
    ) {
        UUID finalUserId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // fallback for dev
        wishlistService.addToWishlist(finalUserId, productId);
        return ResponseEntity.ok(ApiResponse.ok("Added to wishlist"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
        @PathVariable UUID productId,
        @AuthenticationPrincipal UUID userId
    ) {
        UUID finalUserId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // fallback for dev
        wishlistService.removeFromWishlist(finalUserId, productId);
        return ResponseEntity.ok(ApiResponse.ok("Removed from wishlist"));
    }
}

package com.fcs.be.modules.review.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.review.dto.request.CreateReviewRequest;
import com.fcs.be.modules.review.dto.response.ProductReviewResponse;
import com.fcs.be.modules.review.dto.response.ReviewSummaryResponse;
import com.fcs.be.modules.review.service.interfaces.ProductReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductReviewResponse>>> getProductReviews(
        @PathVariable UUID productId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched product reviews", productReviewService.getProductReviews(productId, pageable)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReviewSummaryResponse>> getProductReviewSummary(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched product review summary", productReviewService.getProductReviewSummary(productId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductReviewResponse>> createReview(
        @PathVariable UUID productId,
        @Valid @RequestBody CreateReviewRequest request,
        @AuthenticationPrincipal UUID userId
    ) {
        // Enforce the productId from path matches the request body
        if (!productId.equals(request.productId())) {
            throw new IllegalArgumentException("Product ID in path must match Product ID in request body");
        }

        UUID buyerId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // fallback for dev
        return ResponseEntity.ok(ApiResponse.ok("Review created", productReviewService.createReview(request, buyerId)));
    }
}

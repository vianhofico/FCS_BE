package com.fcs.be.modules.review.service.interfaces;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.review.dto.request.CreateReviewRequest;
import com.fcs.be.modules.review.dto.response.ProductReviewResponse;
import com.fcs.be.modules.review.dto.response.ReviewSummaryResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ProductReviewService {

    PageResponse<ProductReviewResponse> getProductReviews(UUID productId, Pageable pageable);

    ReviewSummaryResponse getProductReviewSummary(UUID productId);

    ProductReviewResponse createReview(CreateReviewRequest request, UUID buyerId);
}

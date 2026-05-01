package com.fcs.be.modules.review.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ProductReviewResponse(
    UUID id,
    UUID productId,
    UUID buyerId,
    String buyerName,
    Integer rating,
    String comment,
    Boolean verifiedPurchase,
    Instant createdAt
) {}

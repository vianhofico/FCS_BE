package com.fcs.be.modules.review.dto.response;

public record ReviewSummaryResponse(
    Double averageRating,
    Long totalReviews,
    Long fiveStarCount,
    Long fourStarCount,
    Long threeStarCount,
    Long twoStarCount,
    Long oneStarCount
) {}

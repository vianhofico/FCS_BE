package com.fcs.be.modules.analytics.dto.response;

import java.math.BigDecimal;

public record SellerAnalyticsResponse(
    Long totalItems,
    Long soldItems,
    BigDecimal totalRevenue,
    BigDecimal pendingWithdrawal
) {}

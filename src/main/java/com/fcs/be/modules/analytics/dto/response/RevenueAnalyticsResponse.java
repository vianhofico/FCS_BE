package com.fcs.be.modules.analytics.dto.response;

import java.math.BigDecimal;

public record RevenueAnalyticsResponse(
    String date,
    BigDecimal revenue,
    Long orders,
    BigDecimal commission
) {}

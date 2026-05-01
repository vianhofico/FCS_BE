package com.fcs.be.modules.analytics.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardAnalyticsResponse(
    BigDecimal totalRevenue,
    Long pendingConsignments,
    Long activeOrders,
    Long pendingWithdrawals,
    BigDecimal revenueToday,
    BigDecimal revenueThisMonth,
    Long newUsersThisMonth,
    List<TopProductResponse> topProducts
) {}

package com.fcs.be.modules.analytics.service.interfaces;

import com.fcs.be.modules.analytics.dto.response.ConsignmentAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.DashboardAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.RevenueAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.SellerAnalyticsResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AnalyticsService {

    DashboardAnalyticsResponse getDashboardMetrics();

    List<RevenueAnalyticsResponse> getRevenueAnalytics(String period, Instant startDate, Instant endDate);

    ConsignmentAnalyticsResponse getConsignmentAnalytics();

    SellerAnalyticsResponse getSellerAnalytics(UUID sellerId);
}

package com.fcs.be.modules.analytics.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.analytics.dto.response.ConsignmentAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.DashboardAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.RevenueAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.SellerAnalyticsResponse;
import com.fcs.be.modules.analytics.service.interfaces.AnalyticsService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardAnalyticsResponse>> getDashboardMetrics() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched dashboard metrics", analyticsService.getDashboardMetrics()));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<List<RevenueAnalyticsResponse>>> getRevenueAnalytics(
        @RequestParam(defaultValue = "DAILY") String period,
        @RequestParam(required = false) Instant startDate,
        @RequestParam(required = false) Instant endDate
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched revenue analytics", analyticsService.getRevenueAnalytics(period, startDate, endDate)));
    }

    @GetMapping("/consignments")
    public ResponseEntity<ApiResponse<ConsignmentAnalyticsResponse>> getConsignmentAnalytics() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched consignment analytics", analyticsService.getConsignmentAnalytics()));
    }

    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<ApiResponse<SellerAnalyticsResponse>> getSellerAnalytics(@PathVariable UUID sellerId) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched seller analytics", analyticsService.getSellerAnalytics(sellerId)));
    }
}

package com.fcs.be.modules.analytics.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.modules.analytics.dto.response.ConsignmentAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.DashboardAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.SellerAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.TopProductResponse;
import com.fcs.be.modules.analytics.service.interfaces.AnalyticsService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void testGetDashboardSuccess() throws Exception {
        DashboardAnalyticsResponse response = new DashboardAnalyticsResponse(
            new BigDecimal("1000000"),
            10L,
            5L,
            2L,
            new BigDecimal("100000"),
            new BigDecimal("800000"),
            20L,
            List.of(new TopProductResponse(UUID.randomUUID(), "SKU1", "Product 1", 10L, new BigDecimal("500000")))
        );
        when(analyticsService.getDashboardMetrics()).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalRevenue").value(1000000));
    }

    @Test
    void testGetRevenueBusinessError() throws Exception {
        when(analyticsService.getRevenueAnalytics(anyString(), any(), any())).thenThrow(new IllegalArgumentException("Invalid period"));

        mockMvc.perform(get("/api/v1/analytics/revenue").param("period", "INVALID"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("BUSINESS_ERROR"));
    }

    @Test
    void testGetConsignmentsSuccess() throws Exception {
        ConsignmentAnalyticsResponse response = new ConsignmentAnalyticsResponse(
            Map.of(ConsignmentRequestStatus.APPROVED, 5L, ConsignmentRequestStatus.SUBMITTED, 3L),
            62.5
        );
        when(analyticsService.getConsignmentAnalytics()).thenReturn(response);

        mockMvc.perform(get("/api/v1/analytics/consignments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.conversionRate").value(62.5));
    }

    @Test
    void testGetSellerAnalyticsSuccess() throws Exception {
        when(analyticsService.getSellerAnalytics(any(UUID.class))).thenReturn(
            new SellerAnalyticsResponse(20L, 10L, new BigDecimal("900000"), new BigDecimal("100000"))
        );

        mockMvc.perform(get("/api/v1/analytics/sellers/{sellerId}", UUID.randomUUID()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.soldItems").value(10));
    }
}

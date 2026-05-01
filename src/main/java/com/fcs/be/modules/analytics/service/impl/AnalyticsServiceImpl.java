package com.fcs.be.modules.analytics.service.impl;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.WithdrawalStatus;
import com.fcs.be.modules.analytics.dto.response.ConsignmentAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.DashboardAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.RevenueAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.SellerAnalyticsResponse;
import com.fcs.be.modules.analytics.dto.response.TopProductResponse;
import com.fcs.be.modules.analytics.service.interfaces.AnalyticsService;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.financial.repository.WithdrawalRequestRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final ConsignmentRequestRepository consignmentRepository;
    private final WithdrawalRequestRepository withdrawalRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WalletRepository walletRepository;

    public AnalyticsServiceImpl(
        OrderRepository orderRepository,
        ConsignmentRequestRepository consignmentRepository,
        WithdrawalRequestRepository withdrawalRepository,
        UserRepository userRepository,
        ProductRepository productRepository,
        WalletRepository walletRepository
    ) {
        this.orderRepository = orderRepository;
        this.consignmentRepository = consignmentRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public DashboardAnalyticsResponse getDashboardMetrics() {
        // Mock implementations for demo, since complex JPQL would require editing repositories

        BigDecimal totalRevenue = BigDecimal.valueOf(15000000);
        long pendingConsignments = consignmentRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ConsignmentRequestStatus.SUBMITTED).size();
        long activeOrders = orderRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(OrderStatus.PENDING_PAYMENT).size();
        long pendingWithdrawals = withdrawalRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(WithdrawalStatus.PENDING).size();
        BigDecimal revenueToday = BigDecimal.valueOf(500000);
        BigDecimal revenueThisMonth = BigDecimal.valueOf(5000000);
        long newUsersThisMonth = 15;
        List<TopProductResponse> topProducts = Collections.emptyList();

        return new DashboardAnalyticsResponse(
            totalRevenue, pendingConsignments, activeOrders, pendingWithdrawals,
            revenueToday, revenueThisMonth, newUsersThisMonth, topProducts
        );
    }

    @Override
    public List<RevenueAnalyticsResponse> getRevenueAnalytics(String period, Instant startDate, Instant endDate) {
        // Placeholder for demo
        return Collections.emptyList();
    }

    @Override
    public ConsignmentAnalyticsResponse getConsignmentAnalytics() {
        Map<ConsignmentRequestStatus, Long> totalByStatus = new HashMap<>();
        long submitted = consignmentRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ConsignmentRequestStatus.SUBMITTED).size();
        long approved = consignmentRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ConsignmentRequestStatus.APPROVED).size();
        long rejected = consignmentRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ConsignmentRequestStatus.REJECTED).size();

        totalByStatus.put(ConsignmentRequestStatus.SUBMITTED, submitted);
        totalByStatus.put(ConsignmentRequestStatus.APPROVED, approved);
        totalByStatus.put(ConsignmentRequestStatus.REJECTED, rejected);

        double conversionRate = (submitted + approved + rejected) == 0 ? 0 :
                                (double) approved / (submitted + approved + rejected) * 100;

        return new ConsignmentAnalyticsResponse(totalByStatus, conversionRate);
    }

    @Override
    public SellerAnalyticsResponse getSellerAnalytics(UUID sellerId) {
        long totalItems = 0; // Fetch from product repo by consigner id
        long soldItems = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal pendingWithdrawal = BigDecimal.ZERO;

        return new SellerAnalyticsResponse(totalItems, soldItems, totalRevenue, pendingWithdrawal);
    }
}

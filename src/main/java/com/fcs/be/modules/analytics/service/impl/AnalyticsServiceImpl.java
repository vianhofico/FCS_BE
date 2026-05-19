package com.fcs.be.modules.analytics.service.impl;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.OrderStatus;
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
import com.fcs.be.modules.order.repository.OrderItemRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ConsignmentRequestRepository consignmentRepository;
    private final WithdrawalRequestRepository withdrawalRepository;
    private final UserRepository userRepository;
    private static final List<OrderStatus> REVENUE_STATUSES = List.of(
        OrderStatus.PAID,
        OrderStatus.CONFIRMED,
        OrderStatus.PACKING,
        OrderStatus.SHIPPED,
        OrderStatus.DELIVERED,
        OrderStatus.COMPLETED
    );
    private static final BigDecimal COMMISSION_RATE = BigDecimal.valueOf(0.15);

    private final ProductRepository productRepository;
    private final WalletRepository walletRepository;

    public AnalyticsServiceImpl(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        ConsignmentRequestRepository consignmentRepository,
        WithdrawalRequestRepository withdrawalRepository,
        UserRepository userRepository,
        ProductRepository productRepository,
        WalletRepository walletRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.consignmentRepository = consignmentRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public DashboardAnalyticsResponse getDashboardMetrics() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        Instant todayStart = now.truncatedTo(ChronoUnit.DAYS).toInstant();
        Instant tomorrowStart = now.truncatedTo(ChronoUnit.DAYS).plusDays(1).toInstant();
        Instant monthStart = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).toInstant();
        Instant nextMonthStart = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).plusMonths(1).toInstant();

        BigDecimal totalRevenue = orderRepository.sumRevenueByStatuses(REVENUE_STATUSES);
        long pendingConsignments = consignmentRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ConsignmentRequestStatus.SUBMITTED).size();
        long activeOrders = orderRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(OrderStatus.PENDING_PAYMENT).size();
        long pendingWithdrawals = withdrawalRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(WithdrawalStatus.PENDING).size();
        BigDecimal revenueToday = orderRepository.sumRevenueByStatusesAndCreatedAtBetween(REVENUE_STATUSES, todayStart, tomorrowStart);
        BigDecimal revenueThisMonth = orderRepository.sumRevenueByStatusesAndCreatedAtBetween(REVENUE_STATUSES, monthStart, nextMonthStart);
        long newUsersThisMonth = userRepository.countByIsDeletedFalseAndCreatedAtGreaterThanEqual(monthStart);
        List<TopProductResponse> topProducts = orderItemRepository.findTopProductsByOrderStatuses(REVENUE_STATUSES);

        return new DashboardAnalyticsResponse(
            totalRevenue, pendingConsignments, activeOrders, pendingWithdrawals,
            revenueToday, revenueThisMonth, newUsersThisMonth, topProducts
        );
    }

    @Override
    public List<RevenueAnalyticsResponse> getRevenueAnalytics(String period, Instant startDate, Instant endDate) {
        ZonedDateTime end = endDate == null
            ? ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS).plusDays(1)
            : ZonedDateTime.ofInstant(endDate, ZoneId.systemDefault());
        ZonedDateTime start = startDate == null
            ? end.minusDays(30)
            : ZonedDateTime.ofInstant(startDate, ZoneId.systemDefault());
        ChronoUnit unit = revenueUnit(period);

        List<RevenueAnalyticsResponse> rows = new ArrayList<>();
        for (ZonedDateTime cursor = start.truncatedTo(ChronoUnit.DAYS); cursor.isBefore(end); cursor = cursor.plus(1, unit)) {
            ZonedDateTime next = cursor.plus(1, unit);
            BigDecimal revenue = orderRepository.sumRevenueByStatusesAndCreatedAtBetween(REVENUE_STATUSES, cursor.toInstant(), next.toInstant());
            Long orders = orderRepository.countByStatusesAndCreatedAtBetween(REVENUE_STATUSES, cursor.toInstant(), next.toInstant());
            rows.add(new RevenueAnalyticsResponse(cursor.toLocalDate().toString(), revenue, orders, revenue.multiply(COMMISSION_RATE)));
        }
        return rows;
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
        long totalItems = productRepository.countBySellerId(sellerId);
        long soldItems = orderItemRepository.countSellerSoldItems(sellerId, REVENUE_STATUSES);
        BigDecimal totalRevenue = orderItemRepository.sumSellerRevenue(sellerId, REVENUE_STATUSES);
        BigDecimal pendingWithdrawal = withdrawalRepository.sumByUserIdAndStatus(sellerId, WithdrawalStatus.PENDING);

        return new SellerAnalyticsResponse(totalItems, soldItems, totalRevenue, pendingWithdrawal);
    }

    private ChronoUnit revenueUnit(String period) {
        if ("MONTHLY".equalsIgnoreCase(period)) {
            return ChronoUnit.MONTHS;
        }
        if ("WEEKLY".equalsIgnoreCase(period)) {
            return ChronoUnit.WEEKS;
        }
        return ChronoUnit.DAYS;
    }
}

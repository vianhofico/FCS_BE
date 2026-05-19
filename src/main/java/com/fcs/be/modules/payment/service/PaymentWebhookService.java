package com.fcs.be.modules.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderStatusHistory;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.order.repository.OrderStatusHistoryRepository;
import com.fcs.be.modules.payment.dto.request.PayOsWebhookRequest;
import com.fcs.be.modules.payment.dto.request.PayOsWebhookRequest.PayOsWebhookData;
import com.fcs.be.modules.payment.entity.PaymentTransaction;
import com.fcs.be.modules.payment.repository.PaymentTransactionRepository;
import com.fcs.be.modules.payment.repository.PaymentSessionRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentWebhookService {
    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookService.class);

    // Use the same provider constant as PayOsPaymentService to avoid mismatches
    private static final String PAYOS_PROVIDER = com.fcs.be.modules.payment.service.PayOsPaymentService.PAYOS_PROVIDER;
    private static final DateTimeFormatter PAYOS_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final PaymentSessionRepository paymentSessionRepository;
    private final PayOsPaymentService payOsPaymentService;
    private final ObjectMapper objectMapper;

    public PaymentWebhookService(
        PaymentTransactionRepository paymentTransactionRepository,
        OrderRepository orderRepository,
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        PayOsPaymentService payOsPaymentService,
        PaymentSessionRepository paymentSessionRepository,
        ObjectMapper objectMapper
    ) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.payOsPaymentService = payOsPaymentService;
        this.paymentSessionRepository = paymentSessionRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handlePayOsWebhook(PayOsWebhookRequest request) {
        PayOsWebhookData data = request.data();
        if (data == null) {
            throw new IllegalArgumentException("Missing PayOS webhook data");
        }

        log.info("Received PayOS webhook: code={}, paymentLinkId={}, orderCode={}, reference={}",
            request.code(), data.paymentLinkId(), data.orderCode(), data.reference());

        Optional<PaymentTransaction> existing = data.reference() == null || data.reference().isBlank()
            ? existingByPaymentLinkId(data.paymentLinkId())
            : paymentTransactionRepository.findByProviderAndReference(PAYOS_PROVIDER, data.reference());

        Order order = resolveOrder(data).orElseGet(() -> existing.map(PaymentTransaction::getOrder).orElse(null));

        PaymentTransaction transaction = existing.orElseGet(() -> paymentTransactionRepository.save(PaymentTransaction.builder()
            .order(order)
            .provider(PAYOS_PROVIDER)
            .providerOrderCode(data.orderCode() == null ? null : data.orderCode().toString())
            .paymentLinkId(data.paymentLinkId())
            .reference(data.reference())
            .amount(data.amount())
            .currency(data.currency())
            .statusCode(data.code())
            .statusDescription(data.desc())
            .accountNumber(data.accountNumber())
            .counterAccountName(data.counterAccountName())
            .counterAccountNumber(data.counterAccountNumber())
            .transactionDateTime(parseTransactionDateTime(data.transactionDateTime()))
            .signature(request.signature())
            .rawPayload(toJson(request))
            .build()));

        if (transaction.getOrder() == null && order != null) {
            transaction.setOrder(order);
            paymentTransactionRepository.save(transaction);
        }

        boolean success = isSuccessful(request, data);
        boolean amountOk = order != null && amountMatches(order, data);
        boolean pending = order != null && order.getStatus() == OrderStatus.PENDING_PAYMENT;

        if (order == null) {
            log.warn("PayOS webhook: could not resolve order for paymentLinkId={}", data.paymentLinkId());
            return;
        }

        if (!success) {
            log.info("PayOS webhook indicates non-success status: request.code={} data.code={}", request.code(), data.code());
            return;
        }

        if (!amountOk) {
            log.warn("PayOS webhook amount mismatch for order {}: orderAmount={} webhookAmount={}",
                order.getId(), order.getTotalAmount(), data.amount());
            return;
        }

        if (!pending) {
            log.info("PayOS webhook: order {} not in PENDING_PAYMENT (status={}), skipping status update",
                order.getId(), order.getStatus());
            return;
        }

        // All checks passed: mark order paid
        order.setStatus(OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);
        payOsPaymentService.markSessionPaid(data.paymentLinkId());
        orderStatusHistoryRepository.save(OrderStatusHistory.builder()
            .order(savedOrder)
            .fromStatus(OrderStatus.PENDING_PAYMENT.name())
            .toStatus(OrderStatus.PAID.name())
            .reason("PayOS payment confirmed: " + data.reference())
            .build());

        log.info("Order {} marked PAID from PayOS webhook (paymentLinkId={}, reference={})",
            savedOrder.getId(), data.paymentLinkId(), data.reference());
    }

    private Optional<PaymentTransaction> existingByPaymentLinkId(String paymentLinkId) {
        if (paymentLinkId == null || paymentLinkId.isBlank()) {
            return Optional.empty();
        }
        return paymentTransactionRepository.findByProviderAndPaymentLinkId(PAYOS_PROVIDER, paymentLinkId);
    }

    private Optional<Order> resolveOrder(PayOsWebhookData data) {
        if (data.description() != null && !data.description().isBlank()) {
            Optional<Order> byDescription = orderRepository.findByOrderCodeAndIsDeletedFalse(data.description());
            if (byDescription.isPresent()) {
                return byDescription;
            }
        }
        if (data.orderCode() != null) {
            Optional<Order> byOrderCode = orderRepository.findByOrderCodeAndIsDeletedFalse(data.orderCode().toString());
            if (byOrderCode.isPresent()) {
                return byOrderCode;
            }
        }
        // Try resolving via payment session (paymentLinkId) as fallback
        if (data.paymentLinkId() != null && !data.paymentLinkId().isBlank()) {
            return paymentSessionRepository.findByPaymentLinkId(data.paymentLinkId())
                .map(session -> session.getOrder());
        }
        return Optional.empty();
    }

    private boolean isSuccessful(PayOsWebhookRequest request, PayOsWebhookData data) {
        return isSuccessCode(request.code()) || isSuccessCode(data.code());
    }

    private boolean isSuccessCode(String code) {
        if (code == null) return false;
        String c = code.trim();
        return "00".equals(c) || "0".equals(c) || "SUCCESS".equalsIgnoreCase(c) || "200".equals(c);
    }

    private boolean amountMatches(Order order, PayOsWebhookData data) {
        return data.amount() != null && order.getTotalAmount().compareTo(data.amount()) == 0;
    }

    private Instant parseTransactionDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, PAYOS_DATE_TIME_FORMAT)
                .atZone(ZoneId.systemDefault())
                .toInstant();
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String toJson(PayOsWebhookRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

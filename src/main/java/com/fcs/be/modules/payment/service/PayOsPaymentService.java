package com.fcs.be.modules.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.config.PayOsProperties;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.payment.dto.response.PaymentQrResponse;
import com.fcs.be.modules.payment.dto.response.PaymentStatusResponse;
import com.fcs.be.modules.payment.entity.PaymentSession;
import com.fcs.be.modules.payment.repository.PaymentSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class PayOsPaymentService {

    private static final Logger log = LoggerFactory.getLogger(PayOsPaymentService.class);
    public static final String PAYOS_PROVIDER = "ONLINE_PAYMENT";
    public static final String PAYOS_API_URL = "https://api-merchant.payos.vn";
    private static final String SESSION_CREATED = "CREATED";
    private static final String SESSION_PAID = "PAID";

    private final OrderRepository orderRepository;
    private final PaymentSessionRepository paymentSessionRepository;
    private final PayOsProperties payOsProperties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public PayOsPaymentService(
        OrderRepository orderRepository,
        PaymentSessionRepository paymentSessionRepository,
        PayOsProperties payOsProperties,
        ObjectMapper objectMapper,
        RestTemplate restTemplate
    ) {
        this.orderRepository = orderRepository;
        this.paymentSessionRepository = paymentSessionRepository;
        this.payOsProperties = payOsProperties;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public PaymentQrResponse createPayOsPayment(UUID orderId) {
        Order order = getOrder(orderId);
        validatePayableOrder(order);

        PaymentSession existingSession = paymentSessionRepository
            .findFirstByOrder_IdAndProviderAndStatusOrderByCreatedAtDesc(order.getId(), PAYOS_PROVIDER, SESSION_CREATED)
            .filter(session -> session.getExpiresAt() == null || session.getExpiresAt().isAfter(Instant.now()))
            .orElse(null);

        if (existingSession != null) {
            return toQrResponse(existingSession);
        }

        PayOsLinkResult linkResult = createPayOsLink(order);
        PaymentSession session = PaymentSession.builder()
            .order(order)
            .provider(PAYOS_PROVIDER)
            .providerOrderCode(linkResult.orderCode())
            .paymentLinkId(linkResult.paymentLinkId())
            .checkoutUrl(linkResult.checkoutUrl())
            .qrCode(linkResult.qrCode())
            .amount(order.getTotalAmount())
            .currency("VND")
            .status(SESSION_CREATED)
            .expiresAt(Instant.now().plus(payOsProperties.paymentTtlMinutes(), ChronoUnit.MINUTES))
            .rawPayload(toJson(linkResult.rawPayload()))
            .build();

        return toQrResponse(paymentSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(UUID orderId) {
        Order order = getOrder(orderId);
        PaymentSession session = paymentSessionRepository
            .findFirstByOrder_IdAndProviderAndStatusOrderByCreatedAtDesc(order.getId(), PAYOS_PROVIDER, SESSION_CREATED)
            .orElse(null);

        if (session == null) {
            return new PaymentStatusResponse(
                order.getId(),
                order.getOrderCode(),
                order.getStatus(),
                null,
                order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CONFIRMED,
                order.getTotalAmount(),
                null,
                null,
                null
            );
        }

        return new PaymentStatusResponse(
            order.getId(),
            order.getOrderCode(),
            order.getStatus(),
            session.getStatus(),
            order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CONFIRMED,
            session.getAmount(),
            session.getExpiresAt(),
            session.getCheckoutUrl(),
            session.getQrCode()
        );
    }

    @Transactional
    public void markSessionPaid(String paymentLinkId) {
        if (paymentLinkId == null || paymentLinkId.isBlank()) {
            return;
        }
        paymentSessionRepository.findByPaymentLinkId(paymentLinkId).ifPresent(session -> {
            session.setStatus(SESSION_PAID);
            paymentSessionRepository.save(session);
        });
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findByIdAndIsDeletedFalse(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    private void validatePayableOrder(Order order) {
        if (!PAYOS_PROVIDER.equalsIgnoreCase(order.getPaymentMethod())) {
            throw new IllegalStateException("Order payment method is not PAYOS");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order is not pending payment");
        }
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Order amount must be greater than zero");
        }
    }

    private PaymentQrResponse toQrResponse(PaymentSession session) {
        Order order = session.getOrder();
        return new PaymentQrResponse(
            order.getId(),
            order.getOrderCode(),
            session.getAmount(),
            session.getCurrency(),
            session.getProvider(),
            session.getPaymentLinkId(),
            session.getCheckoutUrl(),
            session.getQrCode(),
            order.getOrderCode(),
            session.getExpiresAt(),
            session.getStatus(),
            order.getStatus()
        );
    }

    private PayOsLinkResult createPayOsLink(Order order) {
        validatePayOsConfig();

        try {
            // Prepare payment data
            long orderCode = (long) (Math.abs(order.getId().getMostSignificantBits() % 9_000_000_000L) + 1_000_000_000L);
            String description = order.getOrderCode() != null ? order.getOrderCode() : "Order " + order.getId();
            int amountInt = order.getTotalAmount() != null ? order.getTotalAmount().intValue() : 0;

            // Build request body (must be sorted by key for signature)
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("orderCode", orderCode);
            requestBody.put("amount", amountInt);
            requestBody.put("description", description);
            requestBody.put("cancelUrl", payOsProperties.cancelUrl());
            requestBody.put("returnUrl", payOsProperties.returnUrl());

            // Create signature from sorted data
            String signature = createSignature(requestBody, payOsProperties.checksumKey());
            requestBody.put("signature", signature);

            // Add items if needed
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", description);
            item.put("quantity", 1);
            item.put("price", amountInt);
            requestBody.put("items", List.of(item));

            // Make HTTP request to PayOS
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", payOsProperties.clientId());
            headers.set("x-api-key", payOsProperties.apiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Creating PayOS payment link for order {} (code={}, amount={})", 
                order.getId(), orderCode, amountInt);

            Map<String, Object> response = restTemplate.postForObject(
                PAYOS_API_URL + "/v2/payment-requests",
                entity,
                Map.class
            );

            if (response == null || !response.containsKey("data")) {
                throw new IllegalStateException("PayOS API returned unexpected response");
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            String paymentLinkId = asString(data.get("paymentLinkId"));
            String checkoutUrl = asString(data.get("checkoutUrl"));
            String qrCode = asString(data.get("qrCode"));

            if (paymentLinkId == null || paymentLinkId.isBlank()) {
                throw new IllegalStateException("PayOS response missing paymentLinkId");
            }
            if (checkoutUrl == null || checkoutUrl.isBlank()) {
                throw new IllegalStateException("PayOS response missing checkoutUrl");
            }
            if (qrCode == null || qrCode.isBlank()) {
                throw new IllegalStateException("PayOS response missing qrCode");
            }

            log.info("PayOS payment link created successfully: {}", paymentLinkId);
            return new PayOsLinkResult(paymentLinkId, checkoutUrl, qrCode, String.valueOf(orderCode), data);

        } catch (RestClientException e) {
            log.error("HTTP error calling PayOS API: {}", e.getMessage(), e);
            throw new IllegalStateException("Không thể tạo link thanh toán PayOS", e);
        } catch (Exception e) {
            log.error("Unexpected error creating PayOS payment link: {}", e.getMessage(), e);
            throw new IllegalStateException("Không thể tạo link thanh toán PayOS", e);
        }
    }

    private void validatePayOsConfig() {
        if (payOsProperties.clientId() == null || payOsProperties.clientId().isBlank()
            || payOsProperties.apiKey() == null || payOsProperties.apiKey().isBlank()
            || payOsProperties.checksumKey() == null || payOsProperties.checksumKey().isBlank()) {
            throw new IllegalStateException("Thiếu cấu hình PayOS");
        }
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String createSignature(Map<String, Object> data, String checksumKey) {
        // Build signature string from sorted data: amount=$amount&cancelUrl=$cancelUrl&description=$description&orderCode=$orderCode&returnUrl=$returnUrl
        StringBuilder signatureData = new StringBuilder();
        if (data.containsKey("amount")) {
            signatureData.append("amount=").append(data.get("amount")).append("&");
        }
        if (data.containsKey("cancelUrl")) {
            signatureData.append("cancelUrl=").append(data.get("cancelUrl")).append("&");
        }
        if (data.containsKey("description")) {
            signatureData.append("description=").append(data.get("description")).append("&");
        }
        if (data.containsKey("orderCode")) {
            signatureData.append("orderCode=").append(data.get("orderCode")).append("&");
        }
        if (data.containsKey("returnUrl")) {
            signatureData.append("returnUrl=").append(data.get("returnUrl"));
        }

        String dataToSign = signatureData.toString();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Failed to create signature", e);
            throw new RuntimeException("Signature creation failed", e);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private record PayOsLinkResult(
        String paymentLinkId,
        String checkoutUrl,
        String qrCode,
        String orderCode,
        Object rawPayload
    ) {
    }
}

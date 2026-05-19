package com.fcs.be.modules.payment.dto.response;

import com.fcs.be.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentStatusResponse(
    UUID orderId,
    String orderCode,
    OrderStatus orderStatus,
    String paymentStatus,
    boolean paid,
    BigDecimal amount,
    Instant expiresAt,
    String checkoutUrl,
    String qrCode
) {
}

package com.fcs.be.modules.payment.dto.response;

import com.fcs.be.common.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentQrResponse(
    UUID orderId,
    String orderCode,
    BigDecimal amount,
    String currency,
    String provider,
    String paymentLinkId,
    String checkoutUrl,
    String qrCode,
    String transferContent,
    Instant expiresAt,
    String paymentStatus,
    OrderStatus orderStatus
) {
}

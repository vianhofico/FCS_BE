package com.fcs.be.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.payment.payos")
public record PayOsProperties(
    String clientId,
    String apiKey,
    String checksumKey,
    String returnUrl,
    String cancelUrl,
    int paymentTtlMinutes
) {
    public int paymentTtlMinutes() {
        return paymentTtlMinutes <= 0 ? 15 : paymentTtlMinutes;
    }
}

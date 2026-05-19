package com.fcs.be.modules.payment.dto.request;

import java.math.BigDecimal;

public record PayOsWebhookRequest(
    String code,
    String desc,
    PayOsWebhookData data,
    String signature
) {
    public record PayOsWebhookData(
        Long orderCode,
        BigDecimal amount,
        String description,
        String accountNumber,
        String reference,
        String transactionDateTime,
        String paymentLinkId,
        String code,
        String desc,
        String counterAccountBankId,
        String counterAccountBankName,
        String counterAccountName,
        String counterAccountNumber,
        String virtualAccountName,
        String virtualAccountNumber,
        String currency
    ) {}
}

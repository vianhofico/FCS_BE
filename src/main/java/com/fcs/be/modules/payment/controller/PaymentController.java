package com.fcs.be.modules.payment.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.payment.dto.request.PayOsWebhookRequest;
import com.fcs.be.modules.payment.dto.response.PaymentQrResponse;
import com.fcs.be.modules.payment.dto.response.PaymentStatusResponse;
import com.fcs.be.modules.payment.service.PayOsPaymentService;
import com.fcs.be.modules.payment.service.PaymentWebhookService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentWebhookService paymentWebhookService;
    private final PayOsPaymentService payOsPaymentService;

    public PaymentController(PaymentWebhookService paymentWebhookService, PayOsPaymentService payOsPaymentService) {
        this.paymentWebhookService = paymentWebhookService;
        this.payOsPaymentService = payOsPaymentService;
    }

    @PostMapping("/api/v1/payments/orders/{orderId}/payos")
    public ResponseEntity<ApiResponse<PaymentQrResponse>> createPayOsPayment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok("Payment link created", payOsPaymentService.createPayOsPayment(orderId)));
    }

    @PostMapping("/api/v1/payments/orders/{orderId}/online")
    public ResponseEntity<ApiResponse<PaymentQrResponse>> createOnlinePayment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok("Payment link created", payOsPaymentService.createPayOsPayment(orderId)));
    }

    @GetMapping("/api/v1/payments/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<PaymentStatusResponse>> getPaymentStatus(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok("Payment status fetched", payOsPaymentService.getPaymentStatus(orderId)));
    }

    @PostMapping({"/api/v1/payments/webhook", "/api/webhook/payos"})
    public ResponseEntity<ApiResponse<Void>> handlePayOsWebhook(@RequestBody PayOsWebhookRequest request) {
        paymentWebhookService.handlePayOsWebhook(request);
        return ResponseEntity.ok(ApiResponse.ok("PayOS webhook processed"));
    }
}

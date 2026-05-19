package com.fcs.be.modules.payment.repository;

import com.fcs.be.modules.payment.entity.PaymentSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentSessionRepository extends JpaRepository<PaymentSession, UUID> {

    Optional<PaymentSession> findFirstByOrder_IdAndProviderAndStatusOrderByCreatedAtDesc(
        UUID orderId,
        String provider,
        String status
    );

    Optional<PaymentSession> findByPaymentLinkId(String paymentLinkId);

    Optional<PaymentSession> findByProviderAndProviderOrderCode(String provider, String providerOrderCode);
}

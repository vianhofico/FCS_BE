package com.fcs.be.modules.payment.repository;

import com.fcs.be.modules.payment.entity.PaymentTransaction;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByProviderAndReference(String provider, String reference);

    Optional<PaymentTransaction> findByProviderAndPaymentLinkId(String provider, String paymentLinkId);
}

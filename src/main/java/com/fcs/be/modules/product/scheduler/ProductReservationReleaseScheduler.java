package com.fcs.be.modules.product.scheduler;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Releases RESERVED products back to SELLING when reservation timer expires.
 * Runs every 5 minutes to catch unpaid orders.
 */
@Component
public class ProductReservationReleaseScheduler {

    private static final Logger log = LoggerFactory.getLogger(ProductReservationReleaseScheduler.class);

    private final ProductRepository productRepository;

    public ProductReservationReleaseScheduler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000) // every 5 minutes
    @Transactional
    public void releaseExpiredReservations() {
        List<Product> expired = productRepository.findExpiredReservations(Instant.now());
        if (expired.isEmpty()) return;

        log.info("Releasing {} expired product reservations", expired.size());
        for (Product p : expired) {
            p.setStatus(ProductStatus.SELLING);
            p.setReservedUntil(null);
            productRepository.save(p);
            log.debug("Released reservation for product SKU={}", p.getSku());
        }
    }
}

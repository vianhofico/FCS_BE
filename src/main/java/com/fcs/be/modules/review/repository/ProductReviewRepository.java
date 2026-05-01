package com.fcs.be.modules.review.repository;

import com.fcs.be.modules.review.entity.ProductReview;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {

    Page<ProductReview> findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    boolean existsByProductIdAndBuyerIdAndIsDeletedFalse(UUID productId, UUID buyerId);

    @Query("SELECT r.rating, COUNT(r) FROM ProductReview r WHERE r.product.id = :productId AND r.isDeleted = false GROUP BY r.rating")
    List<Object[]> getReviewSummaryByProductId(UUID productId);
}

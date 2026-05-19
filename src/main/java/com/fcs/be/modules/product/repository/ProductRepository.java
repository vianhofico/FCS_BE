package com.fcs.be.modules.product.repository;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.product.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    List<Product> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<Product> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ProductStatus status);

    Optional<Product> findByIdAndIsDeletedFalse(UUID id);

    @Query("""
        select count(p)
        from Product p
        where p.isDeleted = false
          and p.consignmentItem.request.consignor.id = :sellerId
        """)
    Long countBySellerId(@Param("sellerId") UUID sellerId);

    @Query("""
        select count(p)
        from Product p
        where p.isDeleted = false
          and p.consignmentItem.request.consignor.id = :sellerId
          and p.status in :statuses
        """)
    Long countBySellerIdAndStatuses(
        @Param("sellerId") UUID sellerId,
        @Param("statuses") List<ProductStatus> statuses
    );
}

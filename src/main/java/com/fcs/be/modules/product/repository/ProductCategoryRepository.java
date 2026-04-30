package com.fcs.be.modules.product.repository;

import com.fcs.be.modules.product.entity.ProductCategory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

    List<ProductCategory> findByProductIdAndIsDeletedFalse(UUID productId);

    Optional<ProductCategory> findByProductIdAndCategoryIdAndIsDeletedFalse(UUID productId, UUID categoryId);

    @Modifying
    @Query("UPDATE ProductCategory pc SET pc.isDeleted = true WHERE pc.product.id = :productId")
    void softDeleteAllByProductId(UUID productId);
}

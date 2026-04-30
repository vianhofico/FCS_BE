package com.fcs.be.modules.product.repository;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.product.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<Product> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ProductStatus status);

    Optional<Product> findByIdAndIsDeletedFalse(UUID id);
}

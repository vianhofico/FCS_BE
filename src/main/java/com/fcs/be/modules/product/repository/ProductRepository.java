package com.fcs.be.modules.product.repository;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.product.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    List<Product> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<Product> findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(ProductStatus status);

    Optional<Product> findByIdAndIsDeletedFalse(UUID id);
}

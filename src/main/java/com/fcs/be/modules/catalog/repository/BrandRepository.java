package com.fcs.be.modules.catalog.repository;

import com.fcs.be.modules.catalog.entity.Brand;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

    List<Brand> findByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<Brand> findByIdAndIsDeletedFalse(UUID id);
}

package com.fcs.be.modules.catalog.repository;

import com.fcs.be.modules.catalog.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<Category> findByIdAndIsDeletedFalse(UUID id);
}

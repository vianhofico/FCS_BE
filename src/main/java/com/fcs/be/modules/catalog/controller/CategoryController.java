package com.fcs.be.modules.catalog.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.catalog.dto.request.UpsertCategoryRequest;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.service.interfaces.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched categories", categoryService.getCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched category", categoryService.getCategory(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody UpsertCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Category created", categoryService.createCategory(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
        @PathVariable UUID id,
        @Valid @RequestBody UpsertCategoryRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Category updated", categoryService.updateCategory(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.ok("Category deleted"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}
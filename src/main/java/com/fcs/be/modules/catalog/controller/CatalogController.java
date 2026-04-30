package com.fcs.be.modules.catalog.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.catalog.dto.request.UpdateSystemSettingRequest;
import com.fcs.be.modules.catalog.dto.request.UpsertBrandRequest;
import com.fcs.be.modules.catalog.dto.request.UpsertCategoryRequest;
import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import com.fcs.be.modules.catalog.service.interfaces.CatalogService;
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
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched categories", catalogService.getCategories()));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched category", catalogService.getCategory(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody UpsertCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Category created", catalogService.createCategory(request)));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
        @PathVariable UUID id,
        @Valid @RequestBody UpsertCategoryRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Category updated", catalogService.updateCategory(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable UUID id) {
        try {
            catalogService.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.ok("Category deleted"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched brands", catalogService.getBrands()));
    }

    @GetMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrand(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched brand", catalogService.getBrand(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/brands")
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody UpsertBrandRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Brand created", catalogService.createBrand(request)));
    }

    @PutMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
        @PathVariable UUID id,
        @Valid @RequestBody UpsertBrandRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Brand updated", catalogService.updateBrand(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable UUID id) {
        try {
            catalogService.deleteBrand(id);
            return ResponseEntity.ok(ApiResponse.ok("Brand deleted"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<List<SystemSettingResponse>>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched settings", catalogService.getSettings()));
    }

    @PutMapping("/settings/{id}")
    public ResponseEntity<ApiResponse<SystemSettingResponse>> updateSetting(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateSystemSettingRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Setting updated", catalogService.updateSetting(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}

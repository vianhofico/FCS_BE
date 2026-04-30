package com.fcs.be.modules.catalog.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.catalog.dto.request.UpsertBrandRequest;
import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import com.fcs.be.modules.catalog.service.interfaces.BrandService;
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
@RequestMapping("/api/v1/catalog/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched brands", brandService.getBrands()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrand(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched brand", brandService.getBrand(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody UpsertBrandRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Brand created", brandService.createBrand(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
        @PathVariable UUID id,
        @Valid @RequestBody UpsertBrandRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Brand updated", brandService.updateBrand(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable UUID id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.ok(ApiResponse.ok("Brand deleted"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}
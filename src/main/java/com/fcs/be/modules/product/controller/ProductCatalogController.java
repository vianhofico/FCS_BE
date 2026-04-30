package com.fcs.be.modules.product.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.product.dto.request.AssignProductCategoriesRequest;
import com.fcs.be.modules.product.dto.request.CreateWarehouseLogRequest;
import com.fcs.be.modules.product.dto.response.WarehouseLogResponse;
import com.fcs.be.modules.product.service.interfaces.ProductCatalogService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductCatalogController {

    private final ProductCatalogService productCatalogService;

    public ProductCatalogController(ProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched categories", productCatalogService.getProductCategories(id)));
    }

    @PutMapping("/{id}/categories")
    public ResponseEntity<ApiResponse<Void>> assignCategories(
        @PathVariable UUID id,
        @Valid @RequestBody AssignProductCategoriesRequest request
    ) {
        productCatalogService.assignCategories(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Categories assigned"));
    }

    @PostMapping("/warehouse-logs")
    public ResponseEntity<ApiResponse<WarehouseLogResponse>> createWarehouseLog(
        @Valid @RequestBody CreateWarehouseLogRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Warehouse log created", productCatalogService.createWarehouseLog(request)));
    }

    @GetMapping("/{id}/warehouse-logs")
    public ResponseEntity<ApiResponse<List<WarehouseLogResponse>>> getWarehouseLogs(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched warehouse logs", productCatalogService.getWarehouseLogs(id)));
    }
}

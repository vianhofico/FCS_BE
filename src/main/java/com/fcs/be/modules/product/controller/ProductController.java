package com.fcs.be.modules.product.controller;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.product.dto.request.CreateProductRequest;
import com.fcs.be.modules.product.dto.request.UpdateProductRequest;
import com.fcs.be.modules.product.dto.request.UpdateProductStatusRequest;
import com.fcs.be.modules.product.dto.response.ProductResponse;
import com.fcs.be.modules.product.service.interfaces.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
        @RequestParam(required = false) ProductStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched products", productService.getProducts(status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched product", productService.getProduct(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Product created", productService.createProduct(request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateProductRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Product updated", productService.updateProduct(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateProductStatusRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok(
                "Product status updated",
                productService.updateStatus(id, request.status(), request.reason())
            ));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.ok("Product deleted"));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}

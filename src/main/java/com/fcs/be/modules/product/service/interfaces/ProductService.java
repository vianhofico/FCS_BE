package com.fcs.be.modules.product.service.interfaces;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.product.dto.request.CreateProductRequest;
import com.fcs.be.modules.product.dto.request.ProductFilterRequest;
import com.fcs.be.modules.product.dto.request.UpdateProductRequest;
import com.fcs.be.modules.product.dto.response.ProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    PageResponse<ProductResponse> getProducts(ProductFilterRequest filter, Pageable pageable);

    ProductResponse getProduct(UUID id);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(UUID id, UpdateProductRequest request);

    ProductResponse updateStatus(UUID id, ProductStatus status, String reason);

    void deleteProduct(UUID id);
}

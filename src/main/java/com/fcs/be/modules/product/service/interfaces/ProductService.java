package com.fcs.be.modules.product.service.interfaces;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.product.dto.request.CreateProductRequest;
import com.fcs.be.modules.product.dto.request.UpdateProductRequest;
import com.fcs.be.modules.product.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductResponse> getProducts(ProductStatus status);

    ProductResponse getProduct(UUID id);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(UUID id, UpdateProductRequest request);

    ProductResponse updateStatus(UUID id, ProductStatus status, String reason);

    void deleteProduct(UUID id);
}

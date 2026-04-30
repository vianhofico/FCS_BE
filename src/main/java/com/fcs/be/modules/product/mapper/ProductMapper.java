package com.fcs.be.modules.product.mapper;

import com.fcs.be.modules.product.dto.response.ProductResponse;
import com.fcs.be.modules.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getConsignmentItem().getId(),
            product.getBrand() == null ? null : product.getBrand().getId(),
            product.getSku(),
            product.getName(),
            product.getDescription(),
            product.getConditionPercent(),
            product.getOriginalPrice(),
            product.getSalePrice(),
            product.getStatus()
        );
    }
}

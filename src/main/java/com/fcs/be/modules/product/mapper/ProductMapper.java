package com.fcs.be.modules.product.mapper;

import com.fcs.be.modules.product.dto.response.ProductResponse;
import com.fcs.be.modules.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "consignmentItemId", source = "consignmentItem.id")
    @Mapping(target = "brandId", source = "brand.id")
    ProductResponse toResponse(Product product);
}
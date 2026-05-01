package com.fcs.be.modules.review.mapper;

import com.fcs.be.modules.review.dto.response.ProductReviewResponse;
import com.fcs.be.modules.review.entity.ProductReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "buyerName", source = "buyer.username")
    ProductReviewResponse toResponse(ProductReview entity);
}

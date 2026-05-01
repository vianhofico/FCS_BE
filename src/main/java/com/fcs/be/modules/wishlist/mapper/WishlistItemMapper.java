package com.fcs.be.modules.wishlist.mapper;

import com.fcs.be.modules.wishlist.dto.response.WishlistItemResponse;
import com.fcs.be.modules.wishlist.entity.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSalePrice", source = "product.salePrice")
    @Mapping(target = "productStatus", source = "product.status")
    WishlistItemResponse toResponse(WishlistItem entity);
}

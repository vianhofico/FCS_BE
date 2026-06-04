package com.fcs.be.modules.wishlist.mapper;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.modules.product.entity.MediaAsset;
import com.fcs.be.modules.product.repository.MediaAssetRepository;
import com.fcs.be.modules.wishlist.dto.response.WishlistItemResponse;
import com.fcs.be.modules.wishlist.entity.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class WishlistItemMapper {

    @Autowired
    protected MediaAssetRepository mediaAssetRepository;

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productSku", source = "product.sku")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productSalePrice", source = "product.salePrice")
    @Mapping(target = "productStatus", source = "product.status")
    @Mapping(target = "imageUrl", ignore = true)
    public abstract WishlistItemResponse toResponseBase(WishlistItem entity);

    public WishlistItemResponse toResponse(WishlistItem entity) {
        WishlistItemResponse base = toResponseBase(entity);
        String imageUrl = mediaAssetRepository
            .findFirstByOwnerTypeAndOwnerIdAndIsDeletedFalseOrderByIsPrimaryDescDisplayOrderAsc(
                MediaOwnerType.PRODUCT, entity.getProduct().getId())
            .map(MediaAsset::getUrl)
            .orElse(null);
        return new WishlistItemResponse(base.id(), base.productId(), base.productSku(),
            base.productName(), base.productSalePrice(), base.productStatus(), imageUrl);
    }
}

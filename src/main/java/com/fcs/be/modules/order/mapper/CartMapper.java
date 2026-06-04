package com.fcs.be.modules.order.mapper;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.modules.order.dto.response.CartItemResponse;
import com.fcs.be.modules.order.dto.response.CartResponse;
import com.fcs.be.modules.order.entity.Cart;
import com.fcs.be.modules.order.entity.CartItem;
import com.fcs.be.modules.product.entity.MediaAsset;
import com.fcs.be.modules.product.repository.MediaAssetRepository;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class CartMapper {

    @Autowired
    protected MediaAssetRepository mediaAssetRepository;

    @Mapping(target = "userId", source = "cart.user.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "estimatedTotal", expression = "java(calculateEstimatedTotal(items))")
    public abstract CartResponse toResponse(Cart cart, List<CartItem> items);

    @Named("cartItemBase")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "salePrice", source = "product.salePrice")
    @Mapping(target = "productImage", ignore = true)
    public abstract CartItemResponse toResponseBase(CartItem item);

    public CartItemResponse toResponse(CartItem item) {
        CartItemResponse base = toResponseBase(item);
        String imageUrl = mediaAssetRepository
            .findFirstByOwnerTypeAndOwnerIdAndIsDeletedFalseOrderByIsPrimaryDescDisplayOrderAsc(
                MediaOwnerType.PRODUCT, item.getProduct().getId())
            .map(MediaAsset::getUrl)
            .orElse(null);
        return new CartItemResponse(base.id(), base.productId(), base.productName(),
            base.sku(), base.salePrice(), imageUrl);
    }

    BigDecimal calculateEstimatedTotal(List<CartItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
            .map(item -> item.getProduct() == null || item.getProduct().getSalePrice() == null
                ? BigDecimal.ZERO
                : item.getProduct().getSalePrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

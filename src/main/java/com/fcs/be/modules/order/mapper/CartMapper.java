package com.fcs.be.modules.order.mapper;

import com.fcs.be.modules.order.dto.response.CartItemResponse;
import com.fcs.be.modules.order.dto.response.CartResponse;
import com.fcs.be.modules.order.entity.Cart;
import com.fcs.be.modules.order.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "cart.user.id")
    @Mapping(target = "items", source = "items")
    @Mapping(target = "estimatedTotal", expression = "java(calculateEstimatedTotal(items))")
    CartResponse toResponse(Cart cart, List<CartItem> items);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "salePrice", source = "product.salePrice")
    CartItemResponse toResponse(CartItem item);

    default BigDecimal calculateEstimatedTotal(List<CartItem> items) {
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
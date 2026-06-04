package com.fcs.be.modules.order.mapper;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.modules.order.dto.response.OrderItemResponse;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import com.fcs.be.modules.product.entity.MediaAsset;
import com.fcs.be.modules.product.repository.MediaAssetRepository;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

    @Autowired
    protected MediaAssetRepository mediaAssetRepository;

    @Mapping(target = "buyerId", source = "order.buyer.id")
    @Mapping(target = "shippingAddressId", source = "order.shippingAddress.id")
    @Mapping(target = "items", source = "items")
    public abstract OrderResponse toResponse(Order order, List<OrderItem> items);

    @Named("orderItemBase")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productImage", ignore = true)
    public abstract OrderItemResponse toItemResponseBase(OrderItem item);

    public OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse base = toItemResponseBase(item);
        String imageUrl = mediaAssetRepository
            .findFirstByOwnerTypeAndOwnerIdAndIsDeletedFalseOrderByIsPrimaryDescDisplayOrderAsc(
                MediaOwnerType.PRODUCT, item.getProduct().getId())
            .map(MediaAsset::getUrl)
            .orElse(null);
        return new OrderItemResponse(base.id(), base.productId(), base.skuSnapshot(),
            base.productNameSnapshot(), base.conditionSnapshot(), base.priceAtPurchase(), imageUrl);
    }
}

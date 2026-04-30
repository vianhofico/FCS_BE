package com.fcs.be.modules.order.mapper;

import com.fcs.be.modules.order.dto.response.OrderItemResponse;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "buyerId", source = "order.buyer.id")
    @Mapping(target = "shippingAddressId", source = "order.shippingAddress.id")
    @Mapping(target = "items", source = "items")
    OrderResponse toResponse(Order order, List<OrderItem> items);

    @Mapping(target = "productId", source = "product.id")
    OrderItemResponse toItemResponse(OrderItem item);
}
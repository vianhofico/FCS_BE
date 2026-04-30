package com.fcs.be.modules.order.service.interfaces;

import com.fcs.be.modules.order.dto.request.AddCartItemRequest;
import com.fcs.be.modules.order.dto.response.CartResponse;
import java.util.UUID;

public interface CartService {

    CartResponse getCart(UUID userId);

    CartResponse addItem(UUID userId, AddCartItemRequest request);

    CartResponse removeItem(UUID userId, UUID cartItemId);

    void clearCart(UUID userId);
}

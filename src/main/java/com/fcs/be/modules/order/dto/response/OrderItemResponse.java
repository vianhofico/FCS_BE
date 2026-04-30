package com.fcs.be.modules.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID id,
    UUID productId,
    String skuSnapshot,
    String productNameSnapshot,
    String conditionSnapshot,
    BigDecimal priceAtPurchase
) {
}

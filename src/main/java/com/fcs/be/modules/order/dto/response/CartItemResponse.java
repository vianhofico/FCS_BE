package com.fcs.be.modules.order.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
    UUID id,
    UUID productId,
    String productName,
    String sku,
    BigDecimal salePrice
) {}

package com.fcs.be.modules.product.dto.response;

import com.fcs.be.common.enums.ProductStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    UUID consignmentItemId,
    UUID brandId,
    String sku,
    String name,
    String description,
    BigDecimal conditionPercent,
    BigDecimal originalPrice,
    BigDecimal salePrice,
    ProductStatus status
) {
}

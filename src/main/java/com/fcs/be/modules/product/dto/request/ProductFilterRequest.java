package com.fcs.be.modules.product.dto.request;

import com.fcs.be.common.enums.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductFilterRequest(
    String keyword,
    UUID brandId,
    UUID categoryId,
    List<UUID> categoryIds,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    BigDecimal minCondition,
    BigDecimal maxCondition,
    ProductStatus status
) {}

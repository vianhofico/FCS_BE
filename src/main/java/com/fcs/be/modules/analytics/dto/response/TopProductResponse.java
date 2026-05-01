package com.fcs.be.modules.analytics.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TopProductResponse(
    UUID productId,
    String sku,
    String name,
    Long totalSold,
    BigDecimal revenue
) {}

package com.fcs.be.modules.wishlist.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record WishlistItemResponse(
    UUID id,
    UUID productId,
    String productSku,
    String productName,
    BigDecimal productSalePrice,
    String productStatus
) {}

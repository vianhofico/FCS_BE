package com.fcs.be.modules.product.dto.request;

import com.fcs.be.common.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateProductStatusRequest(
    @NotNull ProductStatus status,
    @Size(max = 1000) String reason
) {
}

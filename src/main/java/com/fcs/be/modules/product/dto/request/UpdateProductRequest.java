package com.fcs.be.modules.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
    UUID brandId,
    @NotBlank @Size(max = 255) String name,
    @Size(max = 4000) String description,
    @NotNull @DecimalMin("0.00") BigDecimal conditionPercent,
    @DecimalMin("0.00") BigDecimal originalPrice,
    @NotNull @DecimalMin("0.00") BigDecimal salePrice
) {
}

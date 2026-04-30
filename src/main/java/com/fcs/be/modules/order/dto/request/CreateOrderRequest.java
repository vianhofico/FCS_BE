package com.fcs.be.modules.order.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
    @NotNull UUID buyerId,
    @NotEmpty List<UUID> productIds,
    @NotNull @DecimalMin("0.00") BigDecimal subTotal,
    @NotNull @DecimalMin("0.00") BigDecimal shippingFee,
    @NotNull @DecimalMin("0.00") BigDecimal discountAmount,
    @NotNull @DecimalMin("0.00") BigDecimal totalAmount,
    @Size(max = 50) String paymentMethod,
    UUID shippingAddressId,
    @Size(max = 4000) String shippingSnapshot
) {
}

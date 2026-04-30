package com.fcs.be.modules.order.dto.request;

import com.fcs.be.common.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOrderStatusRequest(
    @NotNull OrderStatus status,
    @Size(max = 1000) String reason
) {
}

package com.fcs.be.modules.order.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddCartItemRequest(
    @NotNull UUID productId
) {}

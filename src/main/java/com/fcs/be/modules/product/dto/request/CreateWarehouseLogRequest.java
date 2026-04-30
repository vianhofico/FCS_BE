package com.fcs.be.modules.product.dto.request;

import com.fcs.be.common.enums.WarehouseActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateWarehouseLogRequest(
    @NotNull UUID productId,
    @NotBlank String location,
    @NotNull WarehouseActionType actionType,
    String note
) {}

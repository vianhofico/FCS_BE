package com.fcs.be.modules.product.dto.response;

import com.fcs.be.common.enums.WarehouseActionType;
import java.time.Instant;
import java.util.UUID;

public record WarehouseLogResponse(
    UUID id,
    UUID productId,
    String location,
    WarehouseActionType actionType,
    String note,
    Instant createdAt
) {}

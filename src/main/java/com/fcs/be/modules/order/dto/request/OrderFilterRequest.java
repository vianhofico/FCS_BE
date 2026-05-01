package com.fcs.be.modules.order.dto.request;

import com.fcs.be.common.enums.OrderStatus;
import java.time.Instant;
import java.util.UUID;

public record OrderFilterRequest(
    String orderCode,
    UUID buyerId,
    OrderStatus status,
    Instant startDate,
    Instant endDate
) {}

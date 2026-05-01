package com.fcs.be.modules.consignment.dto.request;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import java.time.Instant;
import java.util.UUID;

public record ConsignmentFilterRequest(
    String code,
    UUID consignorId,
    ConsignmentRequestStatus status,
    Instant startDate,
    Instant endDate
) {}

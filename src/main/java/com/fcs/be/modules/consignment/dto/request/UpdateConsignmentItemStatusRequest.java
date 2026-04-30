package com.fcs.be.modules.consignment.dto.request;

import com.fcs.be.common.enums.ConsignmentItemStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateConsignmentItemStatusRequest(
    @NotNull ConsignmentItemStatus status,
    String rejectionReason
) {}

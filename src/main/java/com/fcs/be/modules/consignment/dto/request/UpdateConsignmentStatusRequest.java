package com.fcs.be.modules.consignment.dto.request;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateConsignmentStatusRequest(
    @NotNull ConsignmentRequestStatus status,
    @Size(max = 1000) String reason
) {
}

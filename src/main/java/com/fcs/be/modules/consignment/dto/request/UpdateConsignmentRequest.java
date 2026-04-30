package com.fcs.be.modules.consignment.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateConsignmentRequest(
    @Size(max = 1000) String note
) {
}

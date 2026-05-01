package com.fcs.be.modules.return_request.dto.request;

import com.fcs.be.common.enums.ReturnRequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateReturnStatusRequest(
    @NotNull(message = "Status is required")
    ReturnRequestStatus status,

    String reason
) {}

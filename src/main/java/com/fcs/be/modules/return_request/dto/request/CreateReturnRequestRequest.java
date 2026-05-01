package com.fcs.be.modules.return_request.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateReturnRequestRequest(
    @NotNull(message = "Order ID is required")
    UUID orderId,

    @NotBlank(message = "Reason is required")
    String reason,

    String evidenceUrls
) {}

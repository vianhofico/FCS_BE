package com.fcs.be.modules.consignment.dto.request;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateConsignmentRequest(
    @NotNull UUID consignorId,
    @NotBlank @Size(max = 100) String code,
    @NotNull ConsignmentRequestStatus status,
    @Size(max = 1000) String note
) {
}

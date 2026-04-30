package com.fcs.be.modules.consignment.dto.request;

import com.fcs.be.common.enums.ConsignmentContractStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateConsignmentContractStatusRequest(
    @NotNull ConsignmentContractStatus status,
    String reason
) {}

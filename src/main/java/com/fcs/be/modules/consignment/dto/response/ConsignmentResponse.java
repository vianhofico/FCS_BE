package com.fcs.be.modules.consignment.dto.response;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import java.util.UUID;

public record ConsignmentResponse(
    UUID id,
    UUID consignorId,
    String code,
    ConsignmentRequestStatus status,
    String note
) {
}

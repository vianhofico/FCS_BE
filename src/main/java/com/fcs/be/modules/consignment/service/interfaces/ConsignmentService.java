package com.fcs.be.modules.consignment.service.interfaces;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import java.util.List;
import java.util.UUID;

public interface ConsignmentService {

    List<ConsignmentResponse> getConsignments(ConsignmentRequestStatus status);

    ConsignmentResponse getConsignment(UUID id);

    ConsignmentResponse createConsignment(CreateConsignmentRequest request);

    ConsignmentResponse updateConsignment(UUID id, UpdateConsignmentRequest request);

    ConsignmentResponse updateStatus(UUID id, ConsignmentRequestStatus status, String reason);

    void deleteConsignment(UUID id);
}

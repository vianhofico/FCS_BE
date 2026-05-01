package com.fcs.be.modules.consignment.service.interfaces;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.consignment.dto.request.ConsignmentFilterRequest;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ConsignmentService {

    PageResponse<ConsignmentResponse> getConsignments(ConsignmentFilterRequest filter, Pageable pageable);

    ConsignmentResponse getConsignment(UUID id);

    ConsignmentResponse createConsignment(CreateConsignmentRequest request);

    ConsignmentResponse updateConsignment(UUID id, UpdateConsignmentRequest request);

    ConsignmentResponse updateStatus(UUID id, ConsignmentRequestStatus status, String reason);

    void deleteConsignment(UUID id);
}

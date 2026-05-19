package com.fcs.be.modules.consignment.service.interfaces;

import com.fcs.be.modules.consignment.dto.request.CreateConsignmentContractRequest;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.consignment.dto.request.SignConsignmentContractRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentContractStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentContractResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ConsignmentContractService {

    ConsignmentContractResponse createContract(CreateConsignmentContractRequest request);

    PageResponse<ConsignmentContractResponse> getContracts(UUID consignorId, Pageable pageable);

    ConsignmentContractResponse getContractByRequest(UUID requestId);

    ConsignmentContractResponse signContract(
        UUID id,
        UUID userId,
        SignConsignmentContractRequest request,
        String ipAddress,
        String userAgent
    );

    ConsignmentContractResponse updateContractStatus(UUID id, UpdateConsignmentContractStatusRequest request);
}

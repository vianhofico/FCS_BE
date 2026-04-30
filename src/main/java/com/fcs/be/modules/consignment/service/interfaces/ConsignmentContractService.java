package com.fcs.be.modules.consignment.service.interfaces;

import com.fcs.be.modules.consignment.dto.request.CreateConsignmentContractRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentContractStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentContractResponse;
import java.util.UUID;

public interface ConsignmentContractService {

    ConsignmentContractResponse createContract(CreateConsignmentContractRequest request);

    ConsignmentContractResponse getContractByRequest(UUID requestId);

    ConsignmentContractResponse signContract(UUID id);

    ConsignmentContractResponse updateContractStatus(UUID id, UpdateConsignmentContractStatusRequest request);
}

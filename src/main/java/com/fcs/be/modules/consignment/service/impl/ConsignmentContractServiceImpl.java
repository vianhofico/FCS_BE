package com.fcs.be.modules.consignment.service.impl;

import com.fcs.be.common.enums.ConsignmentContractStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentContractRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentContractStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentContractResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentContract;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.mapper.ConsignmentContractMapper;
import com.fcs.be.modules.consignment.repository.ConsignmentContractRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentContractService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsignmentContractServiceImpl implements ConsignmentContractService {

    private final ConsignmentContractRepository contractRepository;
    private final ConsignmentRequestRepository requestRepository;
    private final ConsignmentContractMapper consignmentContractMapper;

    public ConsignmentContractServiceImpl(
        ConsignmentContractRepository contractRepository,
        ConsignmentRequestRepository requestRepository,
        ConsignmentContractMapper consignmentContractMapper
    ) {
        this.contractRepository = contractRepository;
        this.requestRepository = requestRepository;
        this.consignmentContractMapper = consignmentContractMapper;
    }

    @Override
    @Transactional
    public ConsignmentContractResponse createContract(CreateConsignmentContractRequest request) {
        ConsignmentRequest consignmentRequest = requestRepository
            .findByIdAndIsDeletedFalse(request.requestId())
            .orElseThrow(() -> new EntityNotFoundException("Consignment request not found"));

        if (consignmentRequest.getStatus() != ConsignmentRequestStatus.APPROVED) {
            throw new IllegalStateException("Contract can only be created for an APPROVED consignment request");
        }

        if (contractRepository.findByRequestIdAndIsDeletedFalse(request.requestId()).isPresent()) {
            throw new IllegalStateException("A contract already exists for this request");
        }

        ConsignmentContract contract = new ConsignmentContract();
        contract.setRequest(consignmentRequest);
        contract.setCommissionRate(request.commissionRate());
        contract.setAgreedPrice(request.agreedPrice());
        contract.setValidUntil(request.validUntil());
        contract.setStatus(ConsignmentContractStatus.DRAFT);

        return consignmentContractMapper.toResponse(contractRepository.save(contract));
    }

    @Override
    public ConsignmentContractResponse getContractByRequest(UUID requestId) {
        ConsignmentContract contract = contractRepository.findByRequestIdAndIsDeletedFalse(requestId)
            .orElseThrow(() -> new EntityNotFoundException("Contract not found for this request"));
        return consignmentContractMapper.toResponse(contract);
    }

    @Override
    @Transactional
    public ConsignmentContractResponse signContract(UUID id) {
        ConsignmentContract contract = getContractEntity(id);
        if (contract.getStatus() != ConsignmentContractStatus.DRAFT) {
            throw new IllegalStateException("Only a DRAFT contract can be signed");
        }
        contract.setStatus(ConsignmentContractStatus.SIGNED);
        contract.setSignedAt(Instant.now());
        return consignmentContractMapper.toResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public ConsignmentContractResponse updateContractStatus(UUID id, UpdateConsignmentContractStatusRequest request) {
        ConsignmentContract contract = getContractEntity(id);
        contract.setStatus(request.status());
        return consignmentContractMapper.toResponse(contractRepository.save(contract));
    }

    private ConsignmentContract getContractEntity(UUID id) {
        return contractRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
    }
}

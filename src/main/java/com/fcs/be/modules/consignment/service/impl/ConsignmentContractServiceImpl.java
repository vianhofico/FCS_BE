package com.fcs.be.modules.consignment.service.impl;

import com.fcs.be.common.enums.ConsignmentContractStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentContractRequest;
import com.fcs.be.modules.consignment.dto.request.SignConsignmentContractRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentContractStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentContractResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentContract;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.mapper.ConsignmentContractMapper;
import com.fcs.be.modules.consignment.repository.ConsignmentContractRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentContractService;
import jakarta.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public PageResponse<ConsignmentContractResponse> getContracts(UUID consignorId, Pageable pageable) {
        Page<ConsignmentContractResponse> contracts = (consignorId == null
            ? contractRepository.findByIsDeletedFalse(pageable)
            : contractRepository.findByRequestConsignorIdAndIsDeletedFalse(consignorId, pageable))
            .map(consignmentContractMapper::toResponse);
        return PageResponse.of(contracts);
    }

    @Override
    public ConsignmentContractResponse getContractByRequest(UUID requestId) {
        return contractRepository.findByRequestIdAndIsDeletedFalse(requestId)
            .map(consignmentContractMapper::toResponse)
            .orElse(null);
    }

    @Override
    @Transactional
    public ConsignmentContractResponse signContract(
        UUID id,
        UUID userId,
        SignConsignmentContractRequest request,
        String ipAddress,
        String userAgent
    ) {
        if (userId == null) {
            throw new IllegalStateException("Authentication is required to sign a contract");
        }

        ConsignmentContract contract = getContractEntity(id);
        if (contract.getStatus() != ConsignmentContractStatus.DRAFT) {
            throw new IllegalStateException("Only a DRAFT contract can be signed");
        }
        if (!contract.getRequest().getConsignor().getId().equals(userId)) {
            throw new IllegalStateException("Only the consignor can sign this contract");
        }

        Instant signedAt = Instant.now();
        contract.setStatus(ConsignmentContractStatus.SIGNED);
        contract.setSignedAt(signedAt);
        contract.setSignedByUserId(userId);
        contract.setSignedByName(request.signatureName().trim());
        contract.setSignatureMethod("INTERNAL_CONFIRMATION");
        contract.setSignatureIpAddress(ipAddress);
        contract.setSignatureUserAgent(truncate(userAgent, 500));
        contract.setSignatureHash(signatureHash(contract, userId, signedAt));
        return consignmentContractMapper.toResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public ConsignmentContractResponse updateContractStatus(UUID id, UpdateConsignmentContractStatusRequest request) {
        ConsignmentContract contract = getContractEntity(id);
        contract.setStatus(request.status());
        return consignmentContractMapper.toResponse(contractRepository.save(contract));
    }

    private String signatureHash(ConsignmentContract contract, UUID userId, Instant signedAt) {
        String payload = String.join("|",
            contract.getId().toString(),
            contract.getRequest().getId().toString(),
            userId.toString(),
            signedAt.toString(),
            contract.getAgreedPrice().toPlainString(),
            contract.getCommissionRate().toPlainString()
        );
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private ConsignmentContract getContractEntity(UUID id) {
        return contractRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
    }
}

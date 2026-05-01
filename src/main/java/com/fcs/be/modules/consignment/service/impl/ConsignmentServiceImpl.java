package com.fcs.be.modules.consignment.service.impl;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.consignment.dto.request.ConsignmentFilterRequest;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.entity.ConsignmentStatusHistory;
import com.fcs.be.modules.consignment.mapper.ConsignmentMapper;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentSpecification;
import com.fcs.be.modules.consignment.repository.ConsignmentStatusHistoryRepository;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentService;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsignmentServiceImpl implements ConsignmentService {

    private final ConsignmentRequestRepository consignmentRequestRepository;
    private final ConsignmentStatusHistoryRepository consignmentStatusHistoryRepository;
    private final UserRepository userRepository;
    private final ConsignmentMapper consignmentMapper;

    public ConsignmentServiceImpl(
        ConsignmentRequestRepository consignmentRequestRepository,
        ConsignmentStatusHistoryRepository consignmentStatusHistoryRepository,
        UserRepository userRepository,
        ConsignmentMapper consignmentMapper
    ) {
        this.consignmentRequestRepository = consignmentRequestRepository;
        this.consignmentStatusHistoryRepository = consignmentStatusHistoryRepository;
        this.userRepository = userRepository;
        this.consignmentMapper = consignmentMapper;
    }

    @Override
    public PageResponse<ConsignmentResponse> getConsignments(ConsignmentFilterRequest filter, Pageable pageable) {
        return PageResponse.of(
            consignmentRequestRepository.findAll(ConsignmentSpecification.from(filter), pageable)
                .map(consignmentMapper::toResponse)
        );
    }

    @Override
    public ConsignmentResponse getConsignment(UUID id) {
        return consignmentMapper.toResponse(getRequestEntity(id));
    }

    @Override
    @Transactional
    public ConsignmentResponse createConsignment(CreateConsignmentRequest request) {
        User consignor = userRepository.findByIdAndIsDeletedFalse(request.consignorId())
            .orElseThrow(() -> new EntityNotFoundException("Consignor not found"));
        ConsignmentRequest entity = ConsignmentRequest.builder()
            .consignor(consignor)
            .code(request.code())
            .status(request.status())
            .note(request.note())
            .build();
        ConsignmentRequest saved = consignmentRequestRepository.save(entity);
        appendStatusLog(saved, null, saved.getStatus(), "Consignment created");
        return consignmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ConsignmentResponse updateConsignment(UUID id, UpdateConsignmentRequest request) {
        ConsignmentRequest entity = getRequestEntity(id);
        entity.setNote(request.note());
        return consignmentMapper.toResponse(consignmentRequestRepository.save(entity));
    }

    @Override
    @Transactional
    public ConsignmentResponse updateStatus(UUID id, ConsignmentRequestStatus status, String reason) {
        ConsignmentRequest entity = getRequestEntity(id);
        ConsignmentRequestStatus oldStatus = entity.getStatus();
        entity.setStatus(status);
        ConsignmentRequest saved = consignmentRequestRepository.save(entity);
        appendStatusLog(saved, oldStatus, status, reason);
        return consignmentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteConsignment(UUID id) {
        ConsignmentRequest entity = getRequestEntity(id);
        entity.setDeleted(true);
        consignmentRequestRepository.save(entity);
    }

    private ConsignmentRequest getRequestEntity(UUID id) {
        return consignmentRequestRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Consignment not found"));
    }

    @Transactional
    private void appendStatusLog(
        ConsignmentRequest request,
        ConsignmentRequestStatus fromStatus,
        ConsignmentRequestStatus toStatus,
        String reason
    ) {
        ConsignmentStatusHistory history = ConsignmentStatusHistory.builder()
            .entityType("REQUEST")
            .entityId(request.getId())
            .fromStatus(fromStatus == null ? null : fromStatus.name())
            .toStatus(toStatus.name())
            .reason(reason)
            .build();
        consignmentStatusHistoryRepository.save(history);
    }
}

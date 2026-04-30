package com.fcs.be.modules.consignment.service.impl;

import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.entity.ConsignmentStatusHistory;
import com.fcs.be.modules.consignment.mapper.ConsignmentMapper;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentStatusHistoryRepository;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentService;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
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
    public List<ConsignmentResponse> getConsignments(ConsignmentRequestStatus status) {
        List<ConsignmentRequest> requests = status == null
            ? consignmentRequestRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            : consignmentRequestRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(status);
        return requests.stream().map(consignmentMapper::toResponse).toList();
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
        ConsignmentRequest entity = new ConsignmentRequest();
        entity.setConsignor(consignor);
        entity.setCode(request.code());
        entity.setStatus(request.status());
        entity.setNote(request.note());
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

    private void appendStatusLog(
        ConsignmentRequest request,
        ConsignmentRequestStatus fromStatus,
        ConsignmentRequestStatus toStatus,
        String reason
    ) {
        ConsignmentStatusHistory history = new ConsignmentStatusHistory();
        history.setEntityType("REQUEST");
        history.setEntityId(request.getId());
        history.setFromStatus(fromStatus == null ? null : fromStatus.name());
        history.setToStatus(toStatus.name());
        history.setReason(reason);
        consignmentStatusHistoryRepository.save(history);
    }
}

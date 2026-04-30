package com.fcs.be.modules.consignment.service.impl;

import com.fcs.be.common.enums.ConsignmentItemStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.modules.consignment.dto.request.CreateConsignmentItemRequest;
import com.fcs.be.modules.consignment.dto.request.UpdateConsignmentItemStatusRequest;
import com.fcs.be.modules.consignment.dto.response.ConsignmentItemResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.mapper.ConsignmentItemMapper;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.consignment.service.interfaces.ConsignmentItemService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsignmentItemServiceImpl implements ConsignmentItemService {

    private final ConsignmentItemRepository consignmentItemRepository;
    private final ConsignmentRequestRepository consignmentRequestRepository;
    private final ConsignmentItemMapper consignmentItemMapper;

    public ConsignmentItemServiceImpl(
        ConsignmentItemRepository consignmentItemRepository,
        ConsignmentRequestRepository consignmentRequestRepository,
        ConsignmentItemMapper consignmentItemMapper
    ) {
        this.consignmentItemRepository = consignmentItemRepository;
        this.consignmentRequestRepository = consignmentRequestRepository;
        this.consignmentItemMapper = consignmentItemMapper;
    }

    @Override
    @Transactional
    public ConsignmentItemResponse createItem(CreateConsignmentItemRequest request) {
        ConsignmentRequest consignmentRequest = consignmentRequestRepository
            .findByIdAndIsDeletedFalse(request.requestId())
            .orElseThrow(() -> new EntityNotFoundException("Consignment request not found"));

        if (consignmentRequest.getStatus() != ConsignmentRequestStatus.APPROVED) {
            throw new IllegalStateException("Can only add item to an APPROVED consignment request");
        }

        if (consignmentItemRepository.findByRequestIdAndIsDeletedFalse(request.requestId()).isPresent()) {
            throw new IllegalStateException("Consignment item already exists for this request");
        }

        ConsignmentItem item = new ConsignmentItem();
        item.setRequest(consignmentRequest);
        item.setSuggestedName(request.suggestedName());
        item.setSuggestedPrice(request.suggestedPrice());
        item.setConditionNote(request.conditionNote());
        item.setStatus(ConsignmentItemStatus.PROPOSED);

        return consignmentItemMapper.toResponse(consignmentItemRepository.save(item));
    }

    @Override
    public ConsignmentItemResponse getItemByRequest(UUID requestId) {
        ConsignmentItem item = consignmentItemRepository.findByRequestIdAndIsDeletedFalse(requestId)
            .orElseThrow(() -> new EntityNotFoundException("Consignment item not found for request"));
        return consignmentItemMapper.toResponse(item);
    }

    @Override
    @Transactional
    public ConsignmentItemResponse updateItemStatus(UUID id, UpdateConsignmentItemStatusRequest request) {
        ConsignmentItem item = consignmentItemRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Consignment item not found"));

        item.setStatus(request.status());
        if (request.rejectionReason() != null) {
            item.setRejectionReason(request.rejectionReason());
        }

        return consignmentItemMapper.toResponse(consignmentItemRepository.save(item));
    }
}

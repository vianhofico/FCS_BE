package com.fcs.be.modules.consignment.mapper;

import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import org.springframework.stereotype.Component;

@Component
public class ConsignmentMapper {

    public ConsignmentResponse toResponse(ConsignmentRequest request) {
        return new ConsignmentResponse(
            request.getId(),
            request.getConsignor().getId(),
            request.getCode(),
            request.getStatus(),
            request.getNote()
        );
    }
}

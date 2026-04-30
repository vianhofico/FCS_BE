package com.fcs.be.modules.consignment.mapper;

import com.fcs.be.modules.consignment.dto.response.ConsignmentResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsignmentMapper {

    @Mapping(target = "consignorId", source = "consignor.id")
    ConsignmentResponse toResponse(ConsignmentRequest request);
}
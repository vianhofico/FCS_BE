package com.fcs.be.modules.consignment.mapper;

import com.fcs.be.modules.consignment.dto.response.ConsignmentContractResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsignmentContractMapper {

    @Mapping(target = "requestId", source = "request.id")
    ConsignmentContractResponse toResponse(ConsignmentContract contract);
}
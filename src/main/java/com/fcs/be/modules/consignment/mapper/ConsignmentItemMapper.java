package com.fcs.be.modules.consignment.mapper;

import com.fcs.be.modules.consignment.dto.response.ConsignmentItemResponse;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsignmentItemMapper {

    @Mapping(target = "requestId", source = "request.id")
    ConsignmentItemResponse toResponse(ConsignmentItem item);
}
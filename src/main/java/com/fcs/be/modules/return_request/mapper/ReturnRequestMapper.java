package com.fcs.be.modules.return_request.mapper;

import com.fcs.be.modules.return_request.dto.response.ReturnRequestResponse;
import com.fcs.be.modules.return_request.entity.ReturnRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReturnRequestMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "requestedById", source = "requestedBy.id")
    @Mapping(target = "reviewedById", source = "reviewedBy.id")
    ReturnRequestResponse toResponse(ReturnRequest entity);
}

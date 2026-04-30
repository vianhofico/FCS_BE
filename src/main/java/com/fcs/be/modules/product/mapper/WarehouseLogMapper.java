package com.fcs.be.modules.product.mapper;

import com.fcs.be.modules.product.dto.response.WarehouseLogResponse;
import com.fcs.be.modules.product.entity.WarehouseLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseLogMapper {

    @Mapping(target = "productId", source = "product.id")
    WarehouseLogResponse toResponse(WarehouseLog log);
}
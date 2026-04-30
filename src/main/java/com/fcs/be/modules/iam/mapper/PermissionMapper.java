package com.fcs.be.modules.iam.mapper;

import com.fcs.be.modules.iam.dto.response.PermissionResponse;
import com.fcs.be.modules.iam.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);
}
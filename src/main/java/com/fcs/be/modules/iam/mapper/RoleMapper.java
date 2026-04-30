package com.fcs.be.modules.iam.mapper;

import com.fcs.be.modules.iam.dto.response.RoleResponse;
import com.fcs.be.modules.iam.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toResponse(Role role);
}
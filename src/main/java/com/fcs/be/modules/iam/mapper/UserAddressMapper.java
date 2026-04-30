package com.fcs.be.modules.iam.mapper;

import com.fcs.be.modules.iam.dto.response.UserAddressResponse;
import com.fcs.be.modules.iam.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "isDefault", source = "default")
    UserAddressResponse toResponse(UserAddress address);
}
package com.fcs.be.modules.iam.mapper;

import com.fcs.be.modules.iam.dto.response.UserTokenPreviewResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserTokenPreviewMapper {

    UserTokenPreviewResponse toResponse(String accessToken, String refreshToken);
}
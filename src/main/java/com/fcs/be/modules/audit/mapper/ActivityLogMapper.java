package com.fcs.be.modules.audit.mapper;

import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.entity.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    @Mapping(target = "userId", source = "user.id")
    ActivityLogResponse toResponse(ActivityLog activityLog);
}
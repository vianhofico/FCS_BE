package com.fcs.be.modules.audit.mapper;

import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMapper {

    public ActivityLogResponse toResponse(ActivityLog activityLog) {
        return new ActivityLogResponse(
            activityLog.getId(),
            activityLog.getUser() == null ? null : activityLog.getUser().getId(),
            activityLog.getAction(),
            activityLog.getEntityName(),
            activityLog.getEntityId(),
            activityLog.getOldValues(),
            activityLog.getNewValues(),
            activityLog.getIpAddress(),
            activityLog.getUserAgent(),
            activityLog.getCreatedAt()
        );
    }
}
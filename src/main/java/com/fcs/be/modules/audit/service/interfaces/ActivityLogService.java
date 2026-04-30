package com.fcs.be.modules.audit.service.interfaces;

import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import java.util.List;

import com.fcs.be.common.enums.ActivityAction;
import java.util.UUID;

public interface ActivityLogService {

    List<ActivityLogResponse> getActivityLogs();

    void log(UUID userId, ActivityAction action, String entityName, UUID entityId, String oldValues, String newValues, String ipAddress, String userAgent);
}
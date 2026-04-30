package com.fcs.be.modules.audit.service.interfaces;

import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import java.util.List;

public interface ActivityLogService {

    List<ActivityLogResponse> getActivityLogs();
}
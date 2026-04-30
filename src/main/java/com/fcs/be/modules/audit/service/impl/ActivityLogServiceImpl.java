package com.fcs.be.modules.audit.service.impl;

import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.mapper.ActivityLogMapper;
import com.fcs.be.modules.audit.repository.ActivityLogRepository;
import com.fcs.be.modules.audit.service.interfaces.ActivityLogService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, ActivityLogMapper activityLogMapper) {
        this.activityLogRepository = activityLogRepository;
        this.activityLogMapper = activityLogMapper;
    }

    @Override
    public List<ActivityLogResponse> getActivityLogs() {
        return activityLogRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(activityLogMapper::toResponse)
            .toList();
    }
}
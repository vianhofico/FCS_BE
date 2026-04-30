package com.fcs.be.modules.audit.service.impl;

import com.fcs.be.common.enums.ActivityAction;
import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.entity.ActivityLog;
import com.fcs.be.modules.audit.mapper.ActivityLogMapper;
import com.fcs.be.modules.audit.repository.ActivityLogRepository;
import com.fcs.be.modules.audit.service.interfaces.ActivityLogService;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final ActivityLogMapper activityLogMapper;

    public ActivityLogServiceImpl(
        ActivityLogRepository activityLogRepository,
        UserRepository userRepository,
        ActivityLogMapper activityLogMapper
    ) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.activityLogMapper = activityLogMapper;
    }

    @Override
    public List<ActivityLogResponse> getActivityLogs() {
        return activityLogRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(activityLogMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public void log(UUID userId, ActivityAction action, String entityName, UUID entityId, String oldValues, String newValues, String ipAddress, String userAgent) {
        User user = null;
        if (userId != null) {
            user = userRepository.findByIdAndIsDeletedFalse(userId).orElse(null);
        }

        ActivityLog log = ActivityLog.builder()
            .user(user)
            .action(action)
            .entityName(entityName)
            .entityId(entityId)
            .oldValues(oldValues)
            .newValues(newValues)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();
        activityLogRepository.save(log);
    }
}
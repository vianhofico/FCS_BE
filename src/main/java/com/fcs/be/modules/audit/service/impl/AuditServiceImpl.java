package com.fcs.be.modules.audit.service.impl;

import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.mapper.AuditMapper;
import com.fcs.be.modules.audit.repository.ActivityLogRepository;
import com.fcs.be.modules.audit.service.interfaces.AuditService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    private final ActivityLogRepository activityLogRepository;
    private final AuditMapper auditMapper;

    public AuditServiceImpl(ActivityLogRepository activityLogRepository, AuditMapper auditMapper) {
        this.activityLogRepository = activityLogRepository;
        this.auditMapper = auditMapper;
    }

    @Override
    public List<ActivityLogResponse> getActivityLogs() {
        return activityLogRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(auditMapper::toResponse)
            .toList();
    }
}

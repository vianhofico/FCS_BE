package com.fcs.be.modules.audit.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.service.interfaces.AuditService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit/activity-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getActivityLogs() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched activity logs", auditService.getActivityLogs()));
    }
}

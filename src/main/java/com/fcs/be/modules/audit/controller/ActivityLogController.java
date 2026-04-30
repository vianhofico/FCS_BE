package com.fcs.be.modules.audit.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.audit.dto.response.ActivityLogResponse;
import com.fcs.be.modules.audit.service.interfaces.ActivityLogService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> getActivityLogs() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched activity logs", activityLogService.getActivityLogs()));
    }
}
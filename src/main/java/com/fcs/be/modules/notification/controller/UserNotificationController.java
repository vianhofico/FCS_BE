package com.fcs.be.modules.notification.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import com.fcs.be.modules.notification.service.interfaces.UserNotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    public UserNotificationController(UserNotificationService userNotificationService) {
        this.userNotificationService = userNotificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserNotificationResponse>>> getNotifications() {
        return ResponseEntity.ok(ApiResponse.ok(
            "Fetched notifications",
            userNotificationService.getUserNotifications()
        ));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<UserNotificationResponse>> markRead(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", userNotificationService.markRead(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}
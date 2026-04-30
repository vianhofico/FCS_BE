package com.fcs.be.modules.notification.service.interfaces;

import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import java.util.List;
import java.util.UUID;

import com.fcs.be.common.enums.NotificationType;

public interface UserNotificationService {

    List<UserNotificationResponse> getUserNotifications();

    UserNotificationResponse markRead(UUID id);

    void createNotification(UUID userId, NotificationType type, String title, String content, UUID createdBy);
}
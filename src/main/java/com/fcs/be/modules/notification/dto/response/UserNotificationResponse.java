package com.fcs.be.modules.notification.dto.response;

import com.fcs.be.common.enums.NotificationType;
import java.time.Instant;
import java.util.UUID;

public record UserNotificationResponse(
    UUID id,
    UUID userId,
    UUID notificationId,
    String title,
    String content,
    NotificationType type,
    boolean read,
    Instant readAt
) {
}

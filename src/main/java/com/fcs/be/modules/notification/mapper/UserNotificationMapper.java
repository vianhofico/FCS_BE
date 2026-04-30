package com.fcs.be.modules.notification.mapper;

import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import com.fcs.be.modules.notification.entity.UserNotification;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationMapper {

    public UserNotificationResponse toResponse(UserNotification userNotification) {
        return new UserNotificationResponse(
            userNotification.getId(),
            userNotification.getUser().getId(),
            userNotification.getNotification().getId(),
            userNotification.getNotification().getTitle(),
            userNotification.getNotification().getContent(),
            userNotification.getNotification().getType(),
            userNotification.isRead(),
            userNotification.getReadAt()
        );
    }
}
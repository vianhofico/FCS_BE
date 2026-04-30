package com.fcs.be.modules.notification.service.interfaces;

import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import java.util.List;
import java.util.UUID;

public interface UserNotificationService {

    List<UserNotificationResponse> getUserNotifications();

    UserNotificationResponse markRead(UUID id);
}
package com.fcs.be.common.service.notification.interfaces;

import java.util.UUID;

public interface NotificationService {

    void sendToUser(UUID userId, String destination, Object payload);
}

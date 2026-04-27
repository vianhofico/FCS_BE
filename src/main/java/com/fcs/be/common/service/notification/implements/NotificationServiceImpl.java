package com.fcs.be.common.service.notification.implements;

import com.fcs.be.common.service.notification.interfaces.NotificationService;
import java.util.UUID;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void sendToUser(UUID userId, String destination, Object payload) {
        Assert.notNull(userId, "userId must not be null");
        Assert.hasText(destination, "destination must not be blank");
        messagingTemplate.convertAndSendToUser(userId.toString(), destination, payload);
    }
}

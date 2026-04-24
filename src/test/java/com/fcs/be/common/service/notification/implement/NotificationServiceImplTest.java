package com.fcs.be.common.service.notification.implement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.fcs.be.common.service.notification.interfaces.NotificationService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class NotificationServiceImplTest {

    private final SimpMessagingTemplate messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
    private final NotificationService service = new NotificationServiceImpl(messagingTemplate);

    @Test
    void shouldSendNotificationToUserDestination() {
        UUID userId = UUID.randomUUID();
        String destination = "/queue/notifications";
        String payload = "hello";

        service.sendToUser(userId, destination, payload);

        verify(messagingTemplate).convertAndSendToUser(userId.toString(), destination, payload);
    }

    @Test
    void shouldThrowWhenUserIdIsNull() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.sendToUser(null, "/queue/notifications", "payload")
        );
    }
}

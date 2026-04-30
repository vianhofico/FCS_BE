package com.fcs.be.modules.notification.service.impl;

import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import com.fcs.be.common.enums.NotificationType;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.notification.entity.Notification;
import com.fcs.be.modules.notification.entity.UserNotification;
import com.fcs.be.modules.notification.repository.NotificationRepository;
import com.fcs.be.common.service.notification.interfaces.NotificationService;
import com.fcs.be.modules.notification.mapper.UserNotificationMapper;
import com.fcs.be.modules.notification.repository.UserNotificationRepository;
import com.fcs.be.modules.notification.service.interfaces.UserNotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationService websocketNotificationService;
    private final UserNotificationMapper userNotificationMapper;

    public UserNotificationServiceImpl(
        UserNotificationRepository userNotificationRepository,
        NotificationRepository notificationRepository,
        UserRepository userRepository,
        NotificationService websocketNotificationService,
        UserNotificationMapper userNotificationMapper
    ) {
        this.userNotificationRepository = userNotificationRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.websocketNotificationService = websocketNotificationService;
        this.userNotificationMapper = userNotificationMapper;
    }

    @Override
    public List<UserNotificationResponse> getUserNotifications() {
        return userNotificationRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(userNotificationMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public UserNotificationResponse markRead(UUID id) {
        UserNotification userNotification = userNotificationRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        userNotification.setRead(true);
        userNotification.setReadAt(Instant.now());
        return userNotificationMapper.toResponse(userNotificationRepository.save(userNotification));
    }

    @Override
    @Transactional
    public void createNotification(UUID userId, NotificationType type, String title, String content, UUID createdBy) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        User creator = null;
        if (createdBy != null) {
            creator = userRepository.findByIdAndIsDeletedFalse(createdBy).orElse(null);
        }

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setCreatedBy(creator);
        Notification savedNotification = notificationRepository.save(notification);

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setNotification(savedNotification);
        userNotification.setRead(false);
        userNotificationRepository.save(userNotification);

        // Push to WebSocket
        websocketNotificationService.sendToUser(userId, "NEW_NOTIFICATION", savedNotification);
    }
}
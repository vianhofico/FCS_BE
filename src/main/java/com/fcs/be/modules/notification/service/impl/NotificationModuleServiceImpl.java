package com.fcs.be.modules.notification.service.impl;

import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import com.fcs.be.modules.notification.entity.UserNotification;
import com.fcs.be.modules.notification.mapper.NotificationMapper;
import com.fcs.be.modules.notification.repository.UserNotificationRepository;
import com.fcs.be.modules.notification.service.interfaces.NotificationModuleService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationModuleServiceImpl implements NotificationModuleService {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationModuleServiceImpl(
        UserNotificationRepository userNotificationRepository,
        NotificationMapper notificationMapper
    ) {
        this.userNotificationRepository = userNotificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public List<UserNotificationResponse> getUserNotifications() {
        return userNotificationRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(notificationMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public UserNotificationResponse markRead(UUID id) {
        UserNotification userNotification = userNotificationRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Notification not found"));
        userNotification.setRead(true);
        userNotification.setReadAt(Instant.now());
        return notificationMapper.toResponse(userNotificationRepository.save(userNotification));
    }
}

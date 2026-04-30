package com.fcs.be.modules.notification.service.impl;

import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import com.fcs.be.modules.notification.entity.UserNotification;
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
    private final UserNotificationMapper userNotificationMapper;

    public UserNotificationServiceImpl(
        UserNotificationRepository userNotificationRepository,
        UserNotificationMapper userNotificationMapper
    ) {
        this.userNotificationRepository = userNotificationRepository;
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
}
package com.fcs.be.modules.notification.repository;

import com.fcs.be.modules.notification.entity.UserNotification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {

    List<UserNotification> findByIsDeletedFalseOrderByCreatedAtDesc();

    Optional<UserNotification> findByIdAndIsDeletedFalse(UUID id);
}

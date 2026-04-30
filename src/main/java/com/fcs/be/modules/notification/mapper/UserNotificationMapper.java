package com.fcs.be.modules.notification.mapper;

import com.fcs.be.modules.notification.dto.response.UserNotificationResponse;
import com.fcs.be.modules.notification.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserNotificationMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "notificationId", source = "notification.id")
    @Mapping(target = "title", source = "notification.title")
    @Mapping(target = "content", source = "notification.content")
    @Mapping(target = "type", source = "notification.type")
    UserNotificationResponse toResponse(UserNotification userNotification);
}
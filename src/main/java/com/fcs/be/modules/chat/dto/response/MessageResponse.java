package com.fcs.be.modules.chat.dto.response;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    UUID conversationId,
    UUID senderId,
    String senderName,
    String content,
    Instant readAt,
    Instant createdAt
) {}

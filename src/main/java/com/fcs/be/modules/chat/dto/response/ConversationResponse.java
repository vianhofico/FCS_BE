package com.fcs.be.modules.chat.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
    UUID id,
    UUID participant1Id,
    String participant1Name,
    UUID participant2Id,
    String participant2Name,
    Instant lastMessageAt,
    String lastMessagePreview
) {}

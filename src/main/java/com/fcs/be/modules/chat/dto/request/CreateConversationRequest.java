package com.fcs.be.modules.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateConversationRequest(
    @NotNull(message = "Participant ID is required")
    UUID participantId
) {}

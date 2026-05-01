package com.fcs.be.modules.chat.controller;

import com.fcs.be.modules.chat.dto.request.SendMessageRequest;
import com.fcs.be.modules.chat.dto.response.MessageResponse;
import com.fcs.be.modules.chat.service.interfaces.ChatService;
import java.security.Principal;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{conversationId}")
    public void sendMessage(
        @DestinationVariable UUID conversationId,
        @Payload SendMessageRequest request,
        Principal principal
    ) {
        // Ensure conversationId matches
        if (!conversationId.equals(request.conversationId())) {
            throw new IllegalArgumentException("Conversation ID mismatch");
        }

        // Extract user ID from Principal (Assuming it's a JwtAuthenticationToken or similar)
        // For now, assume principal.getName() is the UUID string
        UUID senderId = UUID.fromString(principal.getName());

        MessageResponse response = chatService.saveMessage(request, senderId);

        // Broadcast to the conversation topic
        messagingTemplate.convertAndSend("/topic/chat/" + conversationId, response);
    }
}

package com.fcs.be.modules.chat.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.chat.dto.request.CreateConversationRequest;
import com.fcs.be.modules.chat.dto.request.SendMessageRequest;
import com.fcs.be.modules.chat.dto.response.ConversationResponse;
import com.fcs.be.modules.chat.dto.response.MessageResponse;
import com.fcs.be.modules.chat.service.interfaces.ChatService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ChatService chatService;

    public ConversationController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ConversationResponse>>> getConversations(
        @AuthenticationPrincipal UUID userId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID finalUserId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // dev fallback
        return ResponseEntity.ok(ApiResponse.ok("Fetched conversations", chatService.getConversations(finalUserId, pageable)));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<ApiResponse<PageResponse<MessageResponse>>> getMessages(
        @PathVariable UUID id,
        @AuthenticationPrincipal UUID userId,
        @PageableDefault(size = 50) Pageable pageable
    ) {
        UUID finalUserId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // dev fallback
        return ResponseEntity.ok(ApiResponse.ok("Fetched messages", chatService.getMessages(id, finalUserId, pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConversationResponse>> createConversation(
        @Valid @RequestBody CreateConversationRequest request,
        @AuthenticationPrincipal UUID userId
    ) {
        UUID finalUserId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // dev fallback
        return ResponseEntity.ok(ApiResponse.ok("Conversation created", chatService.createOrGetConversation(request, finalUserId)));
    }

    // A REST endpoint to send messages. For real-time, WebSocket should be used, but this is good for simple integrations.
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
        @Valid @RequestBody SendMessageRequest request,
        @AuthenticationPrincipal UUID userId
    ) {
        UUID finalUserId = userId != null ? userId : UUID.fromString("00000000-0000-0000-0000-000000000000"); // dev fallback
        return ResponseEntity.ok(ApiResponse.ok("Message sent", chatService.saveMessage(request, finalUserId)));
    }
}

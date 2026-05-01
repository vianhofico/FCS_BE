package com.fcs.be.modules.chat.service.interfaces;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.chat.dto.request.CreateConversationRequest;
import com.fcs.be.modules.chat.dto.request.SendMessageRequest;
import com.fcs.be.modules.chat.dto.response.ConversationResponse;
import com.fcs.be.modules.chat.dto.response.MessageResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface ChatService {

    PageResponse<ConversationResponse> getConversations(UUID userId, Pageable pageable);

    PageResponse<MessageResponse> getMessages(UUID conversationId, UUID userId, Pageable pageable);

    ConversationResponse createOrGetConversation(CreateConversationRequest request, UUID userId);

    MessageResponse saveMessage(SendMessageRequest request, UUID senderId);
}

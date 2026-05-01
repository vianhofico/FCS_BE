package com.fcs.be.modules.chat.service.impl;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.chat.dto.request.CreateConversationRequest;
import com.fcs.be.modules.chat.dto.request.SendMessageRequest;
import com.fcs.be.modules.chat.dto.response.ConversationResponse;
import com.fcs.be.modules.chat.dto.response.MessageResponse;
import com.fcs.be.modules.chat.entity.ChatMessage;
import com.fcs.be.modules.chat.entity.Conversation;
import com.fcs.be.modules.chat.mapper.ChatMapper;
import com.fcs.be.modules.chat.repository.ChatMessageRepository;
import com.fcs.be.modules.chat.repository.ConversationRepository;
import com.fcs.be.modules.chat.service.interfaces.ChatService;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    public ChatServiceImpl(
        ConversationRepository conversationRepository,
        ChatMessageRepository chatMessageRepository,
        UserRepository userRepository,
        ChatMapper chatMapper
    ) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
    }

    @Override
    public PageResponse<ConversationResponse> getConversations(UUID userId, Pageable pageable) {
        return PageResponse.of(
            conversationRepository.findUserConversations(userId, pageable)
                .map(chatMapper::toConversationResponse)
        );
    }

    @Override
    public PageResponse<MessageResponse> getMessages(UUID conversationId, UUID userId, Pageable pageable) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.getParticipant1().getId().equals(userId) && !conversation.getParticipant2().getId().equals(userId)) {
            throw new IllegalStateException("You are not a participant in this conversation");
        }

        return PageResponse.of(
            chatMessageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(chatMapper::toMessageResponse)
        );
    }

    @Override
    @Transactional
    public ConversationResponse createOrGetConversation(CreateConversationRequest request, UUID userId) {
        if (userId.equals(request.participantId())) {
            throw new IllegalArgumentException("Cannot create conversation with yourself");
        }

        Optional<Conversation> existing = conversationRepository.findByParticipants(userId, request.participantId());
        if (existing.isPresent()) {
            return chatMapper.toConversationResponse(existing.get());
        }

        User user1 = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user2 = userRepository.findByIdAndIsDeletedFalse(request.participantId())
            .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        Conversation newConversation = Conversation.builder()
            .participant1(user1)
            .participant2(user2)
            .lastMessageAt(Instant.now())
            .lastMessagePreview("")
            .build();

        return chatMapper.toConversationResponse(conversationRepository.save(newConversation));
    }

    @Override
    @Transactional
    public MessageResponse saveMessage(SendMessageRequest request, UUID senderId) {
        Conversation conversation = conversationRepository.findById(request.conversationId())
            .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.getParticipant1().getId().equals(senderId) && !conversation.getParticipant2().getId().equals(senderId)) {
            throw new IllegalStateException("You are not a participant in this conversation");
        }

        User sender = userRepository.findByIdAndIsDeletedFalse(senderId)
            .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        ChatMessage message = ChatMessage.builder()
            .conversation(conversation)
            .sender(sender)
            .content(request.content())
            .build();

        conversation.setLastMessageAt(Instant.now());
        conversation.setLastMessagePreview(request.content().length() > 50 ? request.content().substring(0, 47) + "..." : request.content());
        conversationRepository.save(conversation);

        return chatMapper.toMessageResponse(chatMessageRepository.save(message));
    }
}

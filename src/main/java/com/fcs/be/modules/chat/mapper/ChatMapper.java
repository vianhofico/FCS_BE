package com.fcs.be.modules.chat.mapper;

import com.fcs.be.modules.chat.dto.response.ConversationResponse;
import com.fcs.be.modules.chat.dto.response.MessageResponse;
import com.fcs.be.modules.chat.entity.ChatMessage;
import com.fcs.be.modules.chat.entity.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "participant1Id", source = "participant1.id")
    @Mapping(target = "participant1Name", source = "participant1.username")
    @Mapping(target = "participant2Id", source = "participant2.id")
    @Mapping(target = "participant2Name", source = "participant2.username")
    ConversationResponse toConversationResponse(Conversation entity);

    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", source = "sender.username")
    MessageResponse toMessageResponse(ChatMessage entity);
}

package com.fcs.be.modules.chat.repository;

import com.fcs.be.modules.chat.entity.Conversation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c WHERE (c.participant1.id = :userId OR c.participant2.id = :userId) AND c.isDeleted = false ORDER BY c.lastMessageAt DESC")
    Page<Conversation> findUserConversations(UUID userId, Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE ((c.participant1.id = :user1Id AND c.participant2.id = :user2Id) OR (c.participant1.id = :user2Id AND c.participant2.id = :user1Id)) AND c.isDeleted = false")
    Optional<Conversation> findByParticipants(UUID user1Id, UUID user2Id);
}

package com.codenzic.workspace.communication.infrastructure;
import com.codenzic.workspace.communication.domain.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant,ConversationParticipant.Key> {
    boolean existsByIdConversationIdAndIdUserId(UUID conversationId, UUID userId);
    Optional<ConversationParticipant> findByIdConversationIdAndIdUserId(UUID conversationId, UUID userId);
    List<ConversationParticipant> findAllByIdUserId(UUID userId);
    List<ConversationParticipant> findAllByIdConversationId(UUID conversationId);
}

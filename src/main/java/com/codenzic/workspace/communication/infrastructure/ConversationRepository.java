package com.codenzic.workspace.communication.infrastructure;
import com.codenzic.workspace.communication.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface ConversationRepository extends JpaRepository<Conversation,UUID> {
    List<Conversation> findDistinctByOrganizationIdAndIdIn(UUID organizationId, Collection<UUID> ids);
    Optional<Conversation> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query("""
            select conversation from Conversation conversation
            where conversation.organizationId = :organizationId
              and exists (
                  select participant.id.userId from ConversationParticipant participant
                  where participant.id.conversationId = conversation.id
                    and participant.id.userId = :userId
              )
            """)
    Page<Conversation> findVisibleToUser(
            @Param("organizationId") UUID organizationId,
            @Param("userId") UUID userId,
            Pageable pageable
    );
}

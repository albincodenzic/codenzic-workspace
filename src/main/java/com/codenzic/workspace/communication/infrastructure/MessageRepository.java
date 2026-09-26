package com.codenzic.workspace.communication.infrastructure;
import com.codenzic.workspace.communication.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface MessageRepository extends JpaRepository<Message,UUID> {
    Page<Message> findAllByConversationIdOrderByCreatedAtAsc(UUID conversationId, Pageable pageable);

    @Query("""
            select count(message) from Message message
            where message.conversationId = :conversationId
              and message.senderId <> :userId
              and (:lastReadAt is null or message.createdAt > :lastReadAt)
            """)
    long countUnread(
            @Param("conversationId") UUID conversationId,
            @Param("userId") UUID userId,
            @Param("lastReadAt") java.time.Instant lastReadAt
    );
}

package com.codenzic.workspace.communication.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "conversation_participants") @Getter @NoArgsConstructor
public class ConversationParticipant {
    @EmbeddedId private Key id;
    @Column(name = "joined_at", nullable = false) private Instant joinedAt;
    @Column(name = "last_read_at") private Instant lastReadAt;
    public ConversationParticipant(UUID conversationId, UUID userId) {
        this.id = new Key(conversationId, userId); this.joinedAt = Instant.now();
    }
    public void markRead() { lastReadAt = Instant.now(); }
    @Embeddable @Getter @NoArgsConstructor
    public static class Key {
        @Column(name = "conversation_id") private UUID conversationId;
        @Column(name = "user_id") private UUID userId;
        public Key(UUID conversationId, UUID userId) { this.conversationId = conversationId; this.userId = userId; }
        @Override public boolean equals(Object o) { return o instanceof Key k && conversationId.equals(k.conversationId) && userId.equals(k.userId); }
        @Override public int hashCode() { return 31 * conversationId.hashCode() + userId.hashCode(); }
    }
}

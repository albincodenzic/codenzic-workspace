package com.codenzic.workspace.communication.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "messages") @Getter @NoArgsConstructor
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "conversation_id", nullable = false) private UUID conversationId;
    @Column(name = "sender_id", nullable = false) private UUID senderId;
    @Column(nullable = false, length = 4000) private String body;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    public Message(UUID conversationId, UUID senderId, String body) { this.conversationId=conversationId; this.senderId=senderId; this.body=body; this.createdAt=Instant.now(); }
}

package com.codenzic.workspace.communication.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "conversations") @Getter @NoArgsConstructor
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "organization_id", nullable = false) private UUID organizationId;
    private String title;
    @Column(name = "created_by", nullable = false) private UUID createdBy;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    public Conversation(UUID organizationId, String title, UUID createdBy) {
        this.organizationId = organizationId; this.title = title; this.createdBy = createdBy; this.createdAt = Instant.now();
    }
}

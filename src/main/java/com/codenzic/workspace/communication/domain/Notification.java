package com.codenzic.workspace.communication.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name="notifications") @Getter @NoArgsConstructor
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(name="user_id",nullable=false) private UUID userId;
    @Column(name="organization_id") private UUID organizationId;
    @Column(nullable=false,length=80) private String type;
    @Column(nullable=false,length=200) private String title;
    @Column(nullable=false,length=2000) private String body;
    @Column(name="announcement_id") private UUID announcementId;
    @Column(name="read_at") private Instant readAt;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    public Notification(UUID userId, UUID organizationId, String type, String title, String body, UUID announcementId) { this.userId=userId;this.organizationId=organizationId;this.type=type;this.title=title;this.body=body;this.announcementId=announcementId;this.createdAt=Instant.now(); }
    public void markRead() { readAt = Instant.now(); }
}

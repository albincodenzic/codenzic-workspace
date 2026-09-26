package com.codenzic.workspace.communication.domain;

import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor;
import java.time.Instant; import java.util.UUID;

@Entity @Table(name = "announcements") @Getter @NoArgsConstructor
public class Announcement {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(name="organization_id") private UUID organizationId;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private Scope scope;
    @Column(nullable=false,length=200) private String title;
    @Column(nullable=false,length=10000) private String content;
    @Column(name="created_by",nullable=false) private UUID createdBy;
    @Column(name="status",nullable=false,length=20) private String status="DRAFT";
    @Column(name="target_team_id") private UUID teamId;
    @Column(name="target_department_id") private UUID departmentId;
    @Column(name="published_at") private Instant publishedAt;
    public Announcement(UUID organizationId, Scope scope, String title, String content, UUID createdBy, UUID teamId, UUID departmentId) { this.organizationId=organizationId;this.scope=scope;this.title=title;this.content=content;this.createdBy=createdBy;this.teamId=teamId;this.departmentId=departmentId; }
    public void update(String title, String content, UUID teamId, UUID departmentId) { if(!status.equals("DRAFT")) throw new IllegalStateException("Only draft announcements can be updated");this.title=title;this.content=content;this.teamId=teamId;this.departmentId=departmentId; }
    public void publish() { if(!status.equals("DRAFT")) throw new IllegalStateException("Only draft announcements can be published");status="PUBLISHED";publishedAt=Instant.now(); }
    public void archive() { if(!status.equals("PUBLISHED")) throw new IllegalStateException("Only published announcements can be archived");status="ARCHIVED"; }
    public enum Scope { PLATFORM, ORGANIZATION }
}

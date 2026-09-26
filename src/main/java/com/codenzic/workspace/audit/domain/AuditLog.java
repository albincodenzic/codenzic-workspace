package com.codenzic.workspace.audit.domain;
import jakarta.persistence.*; import lombok.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="audit_logs") @Getter @NoArgsConstructor
public class AuditLog { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @Column(name="organization_id") private UUID organizationId; @Column(name="actor_id") private UUID actorId; @Column(nullable=false,length=80) private String action; @Column(name="resource_type",nullable=false,length=80) private String resourceType; @Column(name="resource_id") private UUID resourceId; @JdbcTypeCode(SqlTypes.JSON) @Column(nullable=false,columnDefinition="jsonb") private String details="{}"; @Column(nullable=false) private Instant createdAt;
 public AuditLog(UUID organizationId,UUID actorId,String action,String resourceType,UUID resourceId,String details){this.organizationId=organizationId;this.actorId=actorId;this.action=action;this.resourceType=resourceType;this.resourceId=resourceId;this.details=details==null?"{}":details;this.createdAt=Instant.now();}
}

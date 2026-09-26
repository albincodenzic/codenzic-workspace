package com.codenzic.workspace.company.domain;

import jakarta.persistence.*; import org.hibernate.annotations.JdbcTypeCode; import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "company_settings")
@Getter @NoArgsConstructor
public class CompanySettings {
    @Id @Column(name = "organization_id") private UUID organizationId;
    @Column(nullable = false, length = 80) private String timezone = "UTC";
    @Column(nullable = false, length = 3) private String currency = "USD";
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "work_week", nullable = false, columnDefinition = "jsonb") private String workWeek = "[\"MONDAY\",\"TUESDAY\",\"WEDNESDAY\",\"THURSDAY\",\"FRIDAY\"]";
    @Column(name = "logo_url", length = 500) private String logoUrl;
    @Column(name = "updated_by") private UUID updatedBy;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    public CompanySettings(UUID organizationId, String timezone, String currency, String workWeek, String logoUrl, UUID updatedBy) {
        this.organizationId=organizationId; this.timezone=timezone; this.currency=currency; this.workWeek=workWeek; this.logoUrl=logoUrl; this.updatedBy=updatedBy; this.updatedAt=Instant.now();
    }
    public void update(String timezone, String currency, String workWeek, String logoUrl, UUID updatedBy) {
        this.timezone=timezone; this.currency=currency; this.workWeek=workWeek; this.logoUrl=logoUrl; this.updatedBy=updatedBy; this.updatedAt=Instant.now();
    }
}

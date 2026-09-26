package com.codenzic.workspace.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "project_members")
@Getter
@NoArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private UUID addedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ProjectMember(UUID organizationId, UUID projectId, UUID employeeId, UUID addedBy) {
        this.organizationId = organizationId;
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.addedBy = addedBy;
    }
}

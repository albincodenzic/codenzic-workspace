package com.codenzic.workspace.team.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_members")
@Getter
@NoArgsConstructor
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private UUID teamId;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private UUID addedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public TeamMember(UUID organizationId, UUID teamId, UUID employeeId, UUID addedBy) {
        this.organizationId = organizationId;
        this.teamId = teamId;
        this.employeeId = employeeId;
        this.addedBy = addedBy;
    }
}

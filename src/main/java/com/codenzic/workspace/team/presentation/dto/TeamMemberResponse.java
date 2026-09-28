package com.codenzic.workspace.team.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Team Member Response payload.")
public record TeamMemberResponse(UUID id, UUID teamId, UUID employeeId, UUID addedBy, Instant createdAt) {
}

package com.codenzic.workspace.project.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Project Member Response payload.")
public record ProjectMemberResponse(UUID id, UUID projectId, UUID employeeId, UUID addedBy, Instant createdAt) {
}

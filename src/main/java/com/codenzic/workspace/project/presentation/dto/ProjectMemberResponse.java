package com.codenzic.workspace.project.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record ProjectMemberResponse(UUID id, UUID projectId, UUID employeeId, UUID addedBy, Instant createdAt) {
}

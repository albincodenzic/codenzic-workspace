package com.codenzic.workspace.team.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record TeamMemberResponse(UUID id, UUID teamId, UUID employeeId, UUID addedBy, Instant createdAt) {
}

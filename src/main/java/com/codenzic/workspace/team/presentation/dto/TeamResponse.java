package com.codenzic.workspace.team.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema; import java.util.UUID; import java.time.LocalDate; @Schema(description = "Team Response payload.") public record TeamResponse(UUID id, UUID organizationId, UUID departmentId, String name, String description) {}
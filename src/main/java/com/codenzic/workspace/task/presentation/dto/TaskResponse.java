package com.codenzic.workspace.task.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema; import java.util.UUID; import java.time.LocalDate; @Schema(description = "Task Response payload.") public record TaskResponse(UUID id, UUID organizationId, UUID projectId, UUID teamId, UUID assigneeId, String title, String description, String status, String priority, LocalDate dueDate) {}
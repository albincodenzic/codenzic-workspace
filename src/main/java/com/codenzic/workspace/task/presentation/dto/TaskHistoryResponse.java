package com.codenzic.workspace.task.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Task History Response payload.")
public record TaskHistoryResponse(UUID id, UUID taskId, UUID actorId, String fieldName, String oldValue, String newValue, Instant createdAt) {
}

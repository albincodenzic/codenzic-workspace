package com.codenzic.workspace.task.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskHistoryResponse(UUID id, UUID taskId, UUID actorId, String fieldName, String oldValue, String newValue, Instant createdAt) {
}

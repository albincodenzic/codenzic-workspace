package com.codenzic.workspace.task.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Task Comment Response payload.")
public record TaskCommentResponse(UUID id, UUID taskId, UUID authorId, String body, Instant createdAt) {
}

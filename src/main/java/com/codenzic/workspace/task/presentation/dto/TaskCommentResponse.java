package com.codenzic.workspace.task.presentation.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskCommentResponse(UUID id, UUID taskId, UUID authorId, String body, Instant createdAt) {
}

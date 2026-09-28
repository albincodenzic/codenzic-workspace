package com.codenzic.workspace.task.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Task Assignment Request payload.")
public record TaskAssignmentRequest(UUID assigneeId) {
}

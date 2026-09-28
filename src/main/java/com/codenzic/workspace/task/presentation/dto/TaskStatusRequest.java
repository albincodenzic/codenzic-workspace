package com.codenzic.workspace.task.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Task Status Request payload.")
public record TaskStatusRequest(@NotBlank @Pattern(regexp = "TODO|IN_PROGRESS|IN_REVIEW|COMPLETED|BLOCKED|CANCELLED") String status) {
}

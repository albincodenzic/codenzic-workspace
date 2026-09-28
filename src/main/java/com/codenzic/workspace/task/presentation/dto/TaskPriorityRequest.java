package com.codenzic.workspace.task.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Task Priority Request payload.")
public record TaskPriorityRequest(@NotBlank @Pattern(regexp = "LOW|MEDIUM|HIGH|URGENT") String priority) {
}

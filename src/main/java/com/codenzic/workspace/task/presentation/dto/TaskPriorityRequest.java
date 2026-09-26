package com.codenzic.workspace.task.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TaskPriorityRequest(@NotBlank @Pattern(regexp = "LOW|MEDIUM|HIGH|URGENT") String priority) {
}

package com.codenzic.workspace.task.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TaskStatusRequest(@NotBlank @Pattern(regexp = "TODO|IN_PROGRESS|IN_REVIEW|COMPLETED|BLOCKED|CANCELLED") String status) {
}

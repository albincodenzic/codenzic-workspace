package com.codenzic.workspace.project.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProjectStatusRequest(@NotBlank @Pattern(regexp = "PLANNING|ACTIVE|ON_HOLD|COMPLETED|CANCELLED") String status) {
}

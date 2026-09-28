package com.codenzic.workspace.project.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Project Status Request payload.")
public record ProjectStatusRequest(@NotBlank @Pattern(regexp = "PLANNING|ACTIVE|ON_HOLD|COMPLETED|CANCELLED") String status) {
}

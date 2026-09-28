package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Refresh Request payload.")
public record RefreshRequest(@NotBlank String refreshToken) {
}

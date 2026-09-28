package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login Request payload.")
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}

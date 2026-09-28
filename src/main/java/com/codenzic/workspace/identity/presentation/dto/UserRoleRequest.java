package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "User Role Request payload.")
public record UserRoleRequest(@NotNull UUID roleId) {
}

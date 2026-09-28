package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@Schema(description = "Role Permission Request payload.")
public record RolePermissionRequest(@NotEmpty Set<@NotBlank String> permissionCodes) {
}

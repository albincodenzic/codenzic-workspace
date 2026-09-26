package com.codenzic.workspace.identity.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record RolePermissionRequest(@NotEmpty Set<@NotBlank String> permissionCodes) {
}

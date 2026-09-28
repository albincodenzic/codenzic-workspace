package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

@Schema(description = "Role Request payload.")
public record RoleRequest(
        @NotBlank @Size(max = 80) String name,
        @Size(max = 255) String description,
        @NotNull Set<@NotBlank String> permissionCodes,
        UUID organizationId
) {
}

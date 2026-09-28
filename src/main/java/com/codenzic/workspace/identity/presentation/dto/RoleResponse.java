package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Role Response payload.")
public record RoleResponse(
        UUID id,
        String name,
        String description,
        UUID organizationId,
        boolean platformRole,
        List<String> permissions
) {
}

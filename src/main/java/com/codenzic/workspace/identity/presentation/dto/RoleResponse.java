package com.codenzic.workspace.identity.presentation.dto;

import java.util.List;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String name,
        String description,
        UUID organizationId,
        boolean platformRole,
        List<String> permissions
) {
}

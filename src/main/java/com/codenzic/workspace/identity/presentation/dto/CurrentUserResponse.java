package com.codenzic.workspace.identity.presentation.dto;

import java.util.List;
import java.util.UUID;

public record CurrentUserResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        UUID organizationId,
        List<String> roles
) {
}

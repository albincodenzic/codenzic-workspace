package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Current User Response payload.")
public record CurrentUserResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        UUID organizationId,
        List<String> roles
) {
}

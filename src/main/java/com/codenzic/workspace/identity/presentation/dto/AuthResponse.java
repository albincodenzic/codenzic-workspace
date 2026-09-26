package com.codenzic.workspace.identity.presentation.dto;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        UUID organizationId,
        List<String> roles
) {
}

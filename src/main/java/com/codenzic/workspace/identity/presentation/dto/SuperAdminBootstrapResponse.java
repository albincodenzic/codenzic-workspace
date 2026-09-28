package com.codenzic.workspace.identity.presentation.dto;

import java.util.UUID;

public record SuperAdminBootstrapResponse(UUID userId, String email) {
}
package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Super Admin Bootstrap Response payload.")
public record SuperAdminBootstrapResponse(UUID userId, String email) {
}
package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Permission Response payload.")
public record PermissionResponse(UUID id, String code, String description) {
}

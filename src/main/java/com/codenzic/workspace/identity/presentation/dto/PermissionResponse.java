package com.codenzic.workspace.identity.presentation.dto;

import java.util.UUID;

public record PermissionResponse(UUID id, String code, String description) {
}

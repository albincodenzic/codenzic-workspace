package com.codenzic.workspace.audit.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Audit Log Response payload.")
public record AuditLogResponse(
        UUID id,
        UUID actorId,
        UUID organizationId,
        String action,
        String entityType,
        UUID entityId,
        Instant timestamp,
        String metadata
) {
}

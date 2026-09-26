package com.codenzic.workspace.audit.presentation.dto;

import java.time.Instant;
import java.util.UUID;

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

package com.codenzic.workspace.project.presentation.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        String status,
        LocalDate startDate,
        LocalDate dueDate,
        UUID managerId,
        int progress
) {
}
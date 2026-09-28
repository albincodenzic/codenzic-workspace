package com.codenzic.workspace.project.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Project Response payload.")
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
package com.codenzic.workspace.department.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.codenzic.workspace.department.domain.DepartmentStatus;

import java.util.UUID;

@Schema(description = "Department Response payload.")
public record DepartmentResponse(UUID id, UUID organizationId, String name, String description, DepartmentStatus status) {
}
package com.codenzic.workspace.department.presentation.dto;

import com.codenzic.workspace.department.domain.DepartmentStatus;

import java.util.UUID;

public record DepartmentResponse(UUID id, UUID organizationId, String name, String description, DepartmentStatus status) {
}
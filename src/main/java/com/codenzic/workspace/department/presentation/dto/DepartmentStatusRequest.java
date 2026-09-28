package com.codenzic.workspace.department.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.codenzic.workspace.department.domain.DepartmentStatus;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Department Status Request payload.")
public record DepartmentStatusRequest(@NotNull DepartmentStatus status) {
}

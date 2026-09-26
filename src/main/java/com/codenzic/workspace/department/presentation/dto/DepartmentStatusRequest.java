package com.codenzic.workspace.department.presentation.dto;

import com.codenzic.workspace.department.domain.DepartmentStatus;
import jakarta.validation.constraints.NotNull;

public record DepartmentStatusRequest(@NotNull DepartmentStatus status) {
}

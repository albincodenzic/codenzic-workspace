package com.codenzic.workspace.employee.presentation.dto;

import com.codenzic.workspace.employee.domain.EmployeeStatus;
import jakarta.validation.constraints.NotNull;

public record EmployeeStatusRequest(@NotNull EmployeeStatus status) {
}

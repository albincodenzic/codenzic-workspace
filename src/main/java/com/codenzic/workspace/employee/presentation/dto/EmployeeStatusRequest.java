package com.codenzic.workspace.employee.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.codenzic.workspace.employee.domain.EmployeeStatus;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Employee Status Request payload.")
public record EmployeeStatusRequest(@NotNull EmployeeStatus status) {
}

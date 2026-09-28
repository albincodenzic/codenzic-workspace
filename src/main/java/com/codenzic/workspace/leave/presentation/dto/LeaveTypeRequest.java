package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Leave Type Request payload.")
public record LeaveTypeRequest(@NotBlank @Size(max = 30) String code, @NotBlank @Size(max = 100) String name,
                               @Min(0) int annualAllowance, @NotNull Boolean active) {
}

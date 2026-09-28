package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Leave Balance Request payload.")
public record LeaveBalanceRequest(@NotNull UUID employeeId, @NotNull UUID leaveTypeId, @Min(2000) int year, @Min(0) int allowanceDays) {
}

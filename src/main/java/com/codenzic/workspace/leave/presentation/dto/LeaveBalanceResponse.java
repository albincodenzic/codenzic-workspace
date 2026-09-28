package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Leave Balance Response payload.")
public record LeaveBalanceResponse(UUID employeeId, UUID leaveTypeId, String leaveTypeCode, String leaveTypeName,
                                   int year, int allowanceDays, int usedDays, int remainingDays) {
}

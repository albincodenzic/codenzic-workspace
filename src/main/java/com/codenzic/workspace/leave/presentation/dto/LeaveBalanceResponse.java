package com.codenzic.workspace.leave.presentation.dto;

import java.util.UUID;

public record LeaveBalanceResponse(UUID employeeId, UUID leaveTypeId, String leaveTypeCode, String leaveTypeName,
                                   int year, int allowanceDays, int usedDays, int remainingDays) {
}

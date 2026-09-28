package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema; import java.time.*; import java.util.UUID; @Schema(description = "Leave Response payload.") public record LeaveResponse(UUID id,UUID organizationId,UUID employeeId,String leaveType,LocalDate startDate,LocalDate endDate,String reason,String status,UUID reviewedBy,Instant reviewedAt,String reviewComment) {}

package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Leave Type Response payload.")
public record LeaveTypeResponse(UUID id, UUID organizationId, String code, String name, int annualAllowance, boolean active) {
}

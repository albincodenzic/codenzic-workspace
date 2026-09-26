package com.codenzic.workspace.leave.presentation.dto;

import java.util.UUID;

public record LeaveTypeResponse(UUID id, UUID organizationId, String code, String name, int annualAllowance, boolean active) {
}

package com.codenzic.workspace.leave.presentation.dto;

import jakarta.validation.constraints.Size;

public record LeaveDecisionRequest(@Size(max = 1000) String comment) {
}

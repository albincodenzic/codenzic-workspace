package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;

@Schema(description = "Leave Decision Request payload.")
public record LeaveDecisionRequest(@Size(max = 1000) String comment) {
}

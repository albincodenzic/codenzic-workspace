package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Leave Review Request payload.")
public record LeaveReviewRequest(@NotBlank @Pattern(regexp = "APPROVED|REJECTED") String status, @Size(max = 1000) String comment) {
}

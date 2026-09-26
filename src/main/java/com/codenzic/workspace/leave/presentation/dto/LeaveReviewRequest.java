package com.codenzic.workspace.leave.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LeaveReviewRequest(@NotBlank @Pattern(regexp = "APPROVED|REJECTED") String status, @Size(max = 1000) String comment) {
}

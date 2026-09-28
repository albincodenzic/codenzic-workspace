package com.codenzic.workspace.attendance.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Attendance Punch Response payload.")
public record AttendancePunchResponse(AttendanceResponse attendance, Instant recordedAt) {
}

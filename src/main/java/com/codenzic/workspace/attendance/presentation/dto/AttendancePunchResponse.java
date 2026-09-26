package com.codenzic.workspace.attendance.presentation.dto;

import java.time.Instant;

public record AttendancePunchResponse(AttendanceResponse attendance, Instant recordedAt) {
}

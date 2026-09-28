package com.codenzic.workspace.attendance.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.*;
import java.util.UUID;
@Schema(description = "Attendance Response payload.")
public record AttendanceResponse(UUID id,UUID organizationId,UUID employeeId,LocalDate attendanceDate,String status,Instant checkIn,Instant checkOut,String notes) {}

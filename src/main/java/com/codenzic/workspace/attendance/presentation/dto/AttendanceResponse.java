package com.codenzic.workspace.attendance.presentation.dto;
import java.time.*;
import java.util.UUID;
public record AttendanceResponse(UUID id,UUID organizationId,UUID employeeId,LocalDate attendanceDate,String status,Instant checkIn,Instant checkOut,String notes) {}

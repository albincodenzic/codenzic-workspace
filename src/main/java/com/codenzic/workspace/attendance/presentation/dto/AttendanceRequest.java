package com.codenzic.workspace.attendance.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.*;
import java.util.UUID;
@Schema(description = "Attendance Request payload.")
public record AttendanceRequest(UUID employeeId,@NotNull LocalDate attendanceDate,@NotNull @Pattern(regexp="PRESENT|ABSENT|LATE|HALF_DAY") String status,
 Instant checkIn,Instant checkOut,@Size(max=500) String notes) {}

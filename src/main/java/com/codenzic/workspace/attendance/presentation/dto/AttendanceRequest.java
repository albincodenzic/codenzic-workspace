package com.codenzic.workspace.attendance.presentation.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.*;
import java.util.UUID;
public record AttendanceRequest(UUID employeeId,@NotNull LocalDate attendanceDate,@NotNull @Pattern(regexp="PRESENT|ABSENT|LATE|HALF_DAY") String status,
 Instant checkIn,Instant checkOut,@Size(max=500) String notes) {}

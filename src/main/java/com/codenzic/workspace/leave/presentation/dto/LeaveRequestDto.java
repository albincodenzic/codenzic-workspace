package com.codenzic.workspace.leave.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
@Schema(description = "Leave Request Dto payload.")
public record LeaveRequestDto(UUID employeeId,@NotBlank @Size(max=30) String leaveType,@NotNull LocalDate startDate,
 @NotNull LocalDate endDate,@Size(max=1000) String reason) {}

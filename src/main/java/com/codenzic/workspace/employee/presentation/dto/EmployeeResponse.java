package com.codenzic.workspace.employee.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import com.codenzic.workspace.employee.domain.EmployeeStatus;
import java.time.LocalDate;

@Schema(description = "Employee Response payload.")
public record EmployeeResponse(
        UUID id,
        UUID organizationId,
        UUID userId,
        String jobTitle,
        String phone,
        boolean active,
        EmployeeStatus status,
        LocalDate dateOfBirth
) {}
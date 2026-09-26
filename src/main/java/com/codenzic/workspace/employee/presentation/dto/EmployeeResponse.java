package com.codenzic.workspace.employee.presentation.dto;
import java.util.UUID;
import com.codenzic.workspace.employee.domain.EmployeeStatus;
import java.time.LocalDate;

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
package com.codenzic.workspace.employee.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
@Schema(description = "Employee Request payload.")
public record EmployeeRequest(@NotNull UUID userId, @Size(max=150) String jobTitle, @Size(max=40) String phone,
                              @PastOrPresent LocalDate dateOfBirth) {}
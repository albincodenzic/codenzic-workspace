package com.codenzic.workspace.employee.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Schema(description = "Employee Update Request payload.")
public record EmployeeUpdateRequest(@Size(max = 150) String jobTitle, @Size(max = 40) String phone,
                                   @PastOrPresent LocalDate dateOfBirth) {
}

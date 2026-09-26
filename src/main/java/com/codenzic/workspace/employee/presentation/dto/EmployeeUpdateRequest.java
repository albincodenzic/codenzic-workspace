package com.codenzic.workspace.employee.presentation.dto;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record EmployeeUpdateRequest(@Size(max = 150) String jobTitle, @Size(max = 40) String phone,
                                   @PastOrPresent LocalDate dateOfBirth) {
}

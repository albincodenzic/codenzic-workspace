package com.codenzic.workspace.project.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Project Request payload.")
public record ProjectRequest(
        @NotBlank @Size(max=200) String name,
        @Size(max=1000) String description,
        @Pattern(regexp="PLANNING|ACTIVE|ON_HOLD|COMPLETED|CANCELLED") String status,
        LocalDate startDate,
        LocalDate dueDate,
        UUID managerId,
        @Min(0) @Max(100) Integer progress
) {}
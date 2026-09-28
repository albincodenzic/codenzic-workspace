package com.codenzic.workspace.department.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Schema(description = "Department Request payload.")
public record DepartmentRequest(@NotBlank @Size(max=150) String name, @Size(max=500) String description) {}
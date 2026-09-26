package com.codenzic.workspace.department.presentation.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record DepartmentRequest(@NotBlank @Size(max=150) String name, @Size(max=500) String description) {}
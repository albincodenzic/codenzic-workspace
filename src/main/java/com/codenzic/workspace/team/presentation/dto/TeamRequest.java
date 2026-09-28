package com.codenzic.workspace.team.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
@Schema(description = "Team Request payload.")
public record TeamRequest(UUID departmentId, @NotBlank @Size(max=150) String name, @Size(max=500) String description) {}
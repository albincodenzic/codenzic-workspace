package com.codenzic.workspace.project.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Project Member Request payload.")
public record ProjectMemberRequest(@NotNull UUID employeeId) {
}

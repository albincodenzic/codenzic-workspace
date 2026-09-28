package com.codenzic.workspace.team.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Team Member Request payload.")
public record TeamMemberRequest(@NotNull UUID employeeId) {
}

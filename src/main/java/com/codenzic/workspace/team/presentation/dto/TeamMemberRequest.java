package com.codenzic.workspace.team.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TeamMemberRequest(@NotNull UUID employeeId) {
}

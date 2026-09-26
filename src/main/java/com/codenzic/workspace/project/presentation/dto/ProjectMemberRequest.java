package com.codenzic.workspace.project.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProjectMemberRequest(@NotNull UUID employeeId) {
}

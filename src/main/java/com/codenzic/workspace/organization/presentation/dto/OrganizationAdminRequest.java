package com.codenzic.workspace.organization.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrganizationAdminRequest(@NotNull UUID userId) {
}

package com.codenzic.workspace.organization.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Organization Admin Request payload.")
public record OrganizationAdminRequest(@NotNull UUID userId) {
}

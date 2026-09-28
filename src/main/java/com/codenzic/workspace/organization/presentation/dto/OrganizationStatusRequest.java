package com.codenzic.workspace.organization.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.codenzic.workspace.organization.domain.OrganizationStatus;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Organization Status Request payload.")
public record OrganizationStatusRequest(@NotNull OrganizationStatus status) {
}

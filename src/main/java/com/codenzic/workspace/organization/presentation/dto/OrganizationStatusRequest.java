package com.codenzic.workspace.organization.presentation.dto;

import com.codenzic.workspace.organization.domain.OrganizationStatus;
import jakarta.validation.constraints.NotNull;

public record OrganizationStatusRequest(@NotNull OrganizationStatus status) {
}

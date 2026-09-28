package com.codenzic.workspace.organization.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.codenzic.workspace.organization.domain.OrganizationStatus;

import java.util.UUID;

@Schema(description = "Organization Response payload.")
public record OrganizationResponse(UUID id, String name, String slug, String description, OrganizationStatus status) {
}
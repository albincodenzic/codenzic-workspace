package com.codenzic.workspace.organization.presentation.dto;

import com.codenzic.workspace.organization.domain.OrganizationStatus;

import java.util.UUID;

public record OrganizationResponse(UUID id, String name, String slug, String description, OrganizationStatus status) {
}
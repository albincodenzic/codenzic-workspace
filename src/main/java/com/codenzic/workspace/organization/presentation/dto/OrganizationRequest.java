package com.codenzic.workspace.organization.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Organization Request payload.")
public record OrganizationRequest(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 120) @Pattern(regexp = "[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*") String slug,
        @Size(max = 500) String description
) {
}
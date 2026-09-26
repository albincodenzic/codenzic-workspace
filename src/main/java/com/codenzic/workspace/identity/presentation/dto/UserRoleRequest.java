package com.codenzic.workspace.identity.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRoleRequest(@NotNull UUID roleId) {
}

package com.codenzic.workspace.identity.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "User Response payload.")
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        UUID organizationId,
        AccountStatus accountStatus,
        boolean platformUser,
        Set<String> roles
) {
    public static UserResponse fromEntity(User user) {
        Set<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet())
                : Set.of();

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getOrganizationId(),
                user.getAccountStatus(),
                user.isPlatformUser(),
                roleNames
        );
    }
}
package com.codenzic.workspace.common.security;

import java.util.Optional;
import java.util.UUID;

public final class CurrentOrganizationContext {

    // InheritableThreadLocal allows child threads to inherit context if needed
    private static final ThreadLocal<UUID> SELECTED_ORGANIZATION = new InheritableThreadLocal<>();

    private CurrentOrganizationContext() {}

    public static UUID selectedOrganizationId() {
        return SELECTED_ORGANIZATION.get();
    }

    public static Optional<UUID> getOptionalSelectedOrganizationId() {
        return Optional.ofNullable(SELECTED_ORGANIZATION.get());
    }

    public static void set(UUID organizationId) {
        SELECTED_ORGANIZATION.set(organizationId);
    }

    public static void clear() {
        SELECTED_ORGANIZATION.remove();
    }
}
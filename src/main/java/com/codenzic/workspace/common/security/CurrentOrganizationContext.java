package com.codenzic.workspace.common.security;

import java.util.UUID;

public final class CurrentOrganizationContext {
    private static final ThreadLocal<UUID> SELECTED_ORGANIZATION = new ThreadLocal<>();

    private CurrentOrganizationContext() {
    }

    public static UUID selectedOrganizationId() {
        return SELECTED_ORGANIZATION.get();
    }

    public static void set(UUID organizationId) {
        SELECTED_ORGANIZATION.set(organizationId);
    }

    public static void clear() {
        SELECTED_ORGANIZATION.remove();
    }
}

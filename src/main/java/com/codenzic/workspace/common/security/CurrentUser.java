package com.codenzic.workspace.common.security;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.identity.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class CurrentUser {
    private CurrentUser() {}
    public static User required() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("No authenticated user is available");
        }
        return user;
    }
    public static UUID id() { return required().getId(); }
    public static UUID organizationId() {
        User user = required();
        UUID selectedOrganizationId = CurrentOrganizationContext.selectedOrganizationId();
        return user.isPlatformUser() ? selectedOrganizationId : user.getOrganizationId();
    }
    public static UUID requiredOrganizationId() {
        UUID organizationId = organizationId();
        if (organizationId == null) throw new BusinessException("ORGANIZATION_REQUIRED", "A platform user must select an organization before accessing tenant data", HttpStatus.FORBIDDEN);
        return organizationId;
    }
    public static boolean hasPermission(String permission) { return required().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(permission)); }
}

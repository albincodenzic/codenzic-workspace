package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.identity.domain.Permission;
import com.codenzic.workspace.identity.domain.Role;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.PermissionRepository;
import com.codenzic.workspace.identity.infrastructure.RoleRepository;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import com.codenzic.workspace.identity.presentation.dto.RoleRequest;
import com.codenzic.workspace.identity.presentation.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private static final String ORGANIZATION_ADMIN_ROLE = "ORGANIZATION_ADMIN";
    private static final List<String> ORGANIZATION_ADMIN_PERMISSIONS = List.of(
            "ORGANIZATION_READ", "ORGANIZATION_UPDATE", "ROLE_MANAGE", "PERMISSION_MANAGE",
            "EMPLOYEE_CREATE", "EMPLOYEE_READ", "EMPLOYEE_UPDATE", "EMPLOYEE_DEACTIVATE",
            "ATTENDANCE_CHECK_IN", "ATTENDANCE_CHECK_OUT", "ATTENDANCE_VIEW_SELF", "ATTENDANCE_VIEW_ORGANIZATION", "ATTENDANCE_MONITOR",
            "LEAVE_APPLY", "LEAVE_CREATE", "LEAVE_READ", "LEAVE_VIEW_SELF", "LEAVE_APPROVE", "LEAVE_MONITOR",
            "LEAVE_TYPE_MANAGE", "LEAVE_BALANCE_MANAGE",
            "EOD_CREATE", "EOD_READ", "EOD_VIEW_SELF", "EOD_REVIEW", "EOD_MONITOR",
            "PROJECT_CREATE", "PROJECT_READ", "PROJECT_UPDATE",
            "TASK_CREATE", "TASK_READ", "TASK_ASSIGN", "TASK_UPDATE",
            "DEPARTMENT_CREATE", "DEPARTMENT_READ", "DEPARTMENT_UPDATE",
            "TEAM_CREATE", "TEAM_READ", "TEAM_UPDATE",
            "CHAT_CREATE", "CHAT_READ", "CHAT_SEND", "ANNOUNCEMENT_CREATE", "ANNOUNCEMENT_READ",
            "ANNOUNCEMENT_UPDATE", "ANNOUNCEMENT_PUBLISH", "NOTIFICATION_READ", "NOTIFICATION_UPDATE",
            "DASHBOARD_VIEW", "REPORT_VIEW", "COMPANY_SETTINGS_READ", "COMPANY_SETTINGS_UPDATE", "AUDIT_LOG_READ"
    );
    private static final Map<String, List<String>> DEFAULT_ORGANIZATION_ROLES = Map.of(
            "HR", List.of("EMPLOYEE_READ", "DEPARTMENT_READ", "TEAM_READ", "ATTENDANCE_MONITOR",
                    "LEAVE_READ", "LEAVE_APPROVE", "LEAVE_MONITOR", "EOD_READ", "EOD_MONITOR", "EOD_REVIEW",
                    "LEAVE_TYPE_MANAGE", "LEAVE_BALANCE_MANAGE", "DASHBOARD_VIEW", "REPORT_VIEW"),
            "TEAM_LEAD", List.of("TEAM_READ", "TEAM_MEMBER_MANAGE", "PROJECT_READ_TEAM", "TASK_READ_TEAM",
                    "TASK_UPDATE_TEAM", "TASK_ASSIGN", "TASK_COMMENT", "EOD_READ_TEAM", "EOD_REVIEW_TEAM"),
            "DEVELOPER", List.of("PROJECT_READ_SELF", "TASK_READ_SELF", "TASK_UPDATE_SELF", "TASK_COMMENT",
                    "ATTENDANCE_CHECK_IN", "ATTENDANCE_CHECK_OUT", "ATTENDANCE_VIEW_SELF",
                    "LEAVE_APPLY", "LEAVE_CREATE", "LEAVE_VIEW_SELF", "EOD_CREATE", "EOD_VIEW_SELF", "EOD_READ_SELF"),
            "EMPLOYEE", List.of("PROJECT_READ_SELF", "TASK_READ_SELF", "TASK_UPDATE_SELF", "TASK_COMMENT",
                    "ATTENDANCE_CHECK_IN", "ATTENDANCE_CHECK_OUT", "ATTENDANCE_VIEW_SELF",
                    "LEAVE_APPLY", "LEAVE_CREATE", "LEAVE_VIEW_SELF", "EOD_CREATE", "EOD_VIEW_SELF", "EOD_READ_SELF"),
            "INTERN", List.of("PROJECT_READ_SELF", "TASK_READ_SELF", "TASK_UPDATE_SELF", "TASK_COMMENT",
                    "ATTENDANCE_CHECK_IN", "ATTENDANCE_CHECK_OUT", "ATTENDANCE_VIEW_SELF",
                    "LEAVE_APPLY", "LEAVE_CREATE", "LEAVE_VIEW_SELF", "EOD_CREATE", "EOD_VIEW_SELF", "EOD_READ_SELF")
    );

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final AuditLogService audit;

    @Transactional(readOnly = true)
    public List<RoleResponse> list() {
        if (CurrentUser.required().isPlatformUser()) {
            return roleRepository.findAllWithPermissions().stream().map(this::toResponse).toList();
        }
        return roleRepository.findAllByOrganizationId(CurrentUser.requiredOrganizationId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse get(UUID id) {
        return toResponse(findVisibleRole(id));
    }

    @Transactional
    public RoleResponse create(RoleRequest request) {
        UUID organizationId = CurrentUser.required().isPlatformUser()
                ? request.organizationId()
                : CurrentUser.requiredOrganizationId();
        if (organizationId == null) {
            throw new BusinessException("ORGANIZATION_REQUIRED", "An organization is required to create a role", HttpStatus.BAD_REQUEST);
        }
        if (roleRepository.existsByOrganizationIdAndNameIgnoreCase(organizationId, request.name().trim())) {
            throw new BusinessException("ROLE_NAME_EXISTS", "A role with this name already exists in the organization", HttpStatus.CONFLICT);
        }
        Role role = new Role(request.name().trim(), request.description(), organizationId, false);
        role.replacePermissions(resolvePermissions(request.permissionCodes()));
        Role saved = roleRepository.save(role);
        audit.record(saved.getOrganizationId(), CurrentUser.id(), "CREATE", "ROLE", saved.getId(), "{}");
        return toResponse(saved);
    }

    @Transactional
    public RoleResponse update(UUID id, RoleRequest request) {
        Role role = findVisibleRole(id);
        if (role.getName().equalsIgnoreCase(ORGANIZATION_ADMIN_ROLE)) {
            throw new BusinessException("SYSTEM_ROLE_PROTECTED", "The organization administrator role cannot be modified", HttpStatus.FORBIDDEN);
        }

        if (roleRepository.existsByOrganizationIdAndNameIgnoreCaseAndIdNot(
                role.getOrganizationId(), request.name().trim(), id)) {
            throw new BusinessException("ROLE_NAME_EXISTS", "A role with this name already exists in the organization", HttpStatus.CONFLICT);
        }
        role.update(request.name().trim(), request.description());
        role.replacePermissions(resolvePermissions(request.permissionCodes()));
        audit.record(role.getOrganizationId(), CurrentUser.id(), "UPDATE", "ROLE", role.getId(), "{}");
        return toResponse(role);
    }

    @Transactional
    public RoleResponse assignPermissions(UUID roleId, Set<String> permissionCodes) {
        Role role = findVisibleRole(roleId);
        Set<Permission> permissions = new HashSet<>(role.getPermissions());
        permissions.addAll(resolvePermissions(permissionCodes));
        role.replacePermissions(permissions);
        audit.record(role.getOrganizationId(), CurrentUser.id(), "PERMISSIONS_UPDATE", "ROLE", role.getId(), "{}");
        return toResponse(role);
    }

    @Transactional
    public void delete(UUID id) {
        Role role = findVisibleRole(id);
        if (role.getName().equalsIgnoreCase(ORGANIZATION_ADMIN_ROLE)) {
            throw new BusinessException("SYSTEM_ROLE_PROTECTED", "The organization administrator role cannot be deleted", HttpStatus.FORBIDDEN);
        }
        roleRepository.deleteUserAssignments(id);
        roleRepository.deleteRolePermissions(id);
        roleRepository.delete(role);
        audit.record(role.getOrganizationId(), CurrentUser.id(), "DELETE", "ROLE", id, "{}");
    }

    @Transactional
    public void assign(UUID roleId, UUID userId) {
        Role role = findVisibleRole(roleId);
        User user = userRepository.findWithRolesById(userId)
                .orElseThrow(() -> notFound("USER_NOT_FOUND", "User not found"));
        if (role.isPlatformRole()) {
            if (!CurrentUser.required().isPlatformUser() || !user.isPlatformUser()) {
                throw forbidden();
            }
        } else if (!role.getOrganizationId().equals(user.getOrganizationId())) {
            throw notFound("USER_NOT_FOUND", "User not found in this organization");
        }
        if (!user.getRoles().contains(role)) {
            user.assignRole(role);
            audit.record(role.getOrganizationId(), CurrentUser.id(), "ROLE_ASSIGNED", "USER", userId,
                    "{\"roleId\":\"" + roleId + "\"}");
        }
    }

    @Transactional
    public void unassign(UUID roleId, UUID userId) {
        Role role = findVisibleRole(roleId);
        User user = userRepository.findWithRolesById(userId)
                .orElseThrow(() -> notFound("USER_NOT_FOUND", "User not found"));
        if (role.isPlatformRole() && (!CurrentUser.required().isPlatformUser() || !user.isPlatformUser())) {
            throw forbidden();
        }
        if (!role.isPlatformRole() && !role.getOrganizationId().equals(user.getOrganizationId())) {
            throw notFound("USER_NOT_FOUND", "User not found in this organization");
        }
        if (user.getRoles().contains(role)) {
            user.removeRole(role);
            audit.record(role.getOrganizationId(), CurrentUser.id(), "ROLE_REMOVED", "USER", userId,
                    "{\"roleId\":\"" + roleId + "\"}");
        }
    }

    @Transactional
    public void assignOrganizationAdmin(UUID organizationId, UUID userId) {
        requirePlatformUser();
        verifyOrganizationScope(organizationId);
        User user = userRepository.findWithRolesById(userId)
                .filter(candidate -> organizationId.equals(candidate.getOrganizationId()) && !candidate.isPlatformUser())
                .orElseThrow(() -> notFound("USER_NOT_FOUND", "User not found in this organization"));
        Role role = getOrCreateOrganizationAdminRole(organizationId);
        if (!user.getRoles().contains(role)) {
            user.assignRole(role);
            audit.record(organizationId, CurrentUser.id(), "ADMIN_ASSIGNED", "ORGANIZATION", organizationId,
                    "{\"userId\":\"" + userId + "\"}");
        }
    }

    @Transactional
    public void unassignOrganizationAdmin(UUID organizationId, UUID userId) {
        requirePlatformUser();
        verifyOrganizationScope(organizationId);
        User user = userRepository.findWithRolesById(userId)
                .filter(candidate -> organizationId.equals(candidate.getOrganizationId()) && !candidate.isPlatformUser())
                .orElseThrow(() -> notFound("USER_NOT_FOUND", "User not found in this organization"));
        roleRepository.findByOrganizationIdAndName(organizationId, ORGANIZATION_ADMIN_ROLE)
                .filter(user.getRoles()::contains)
                .ifPresent(role -> {
                    user.removeRole(role);
                    audit.record(organizationId, CurrentUser.id(), "ADMIN_REMOVED", "ORGANIZATION", organizationId,
                            "{\"userId\":\"" + userId + "\"}");
                });
    }

    @Transactional
    public void ensureOrganizationAdminRole(UUID organizationId) {
        ensureOrganizationAdminRoleEntity(organizationId);
    }

    @Transactional
    public void ensureOrganizationDefaultRoles(UUID organizationId) {
        ensureOrganizationAdminRoleEntity(organizationId);
        DEFAULT_ORGANIZATION_ROLES.forEach((name, codes) -> roleRepository.findByOrganizationIdAndName(organizationId, name)
                .orElseGet(() -> {
                    Role role = new Role(name, name.replace('_', ' ').toLowerCase(Locale.ROOT), organizationId, false);
                    role.replacePermissions(resolvePermissions(Set.copyOf(codes)));
                    return roleRepository.save(role);
                }));
    }

    private Role getOrCreateOrganizationAdminRole(UUID organizationId) {
        return roleRepository.findByOrganizationIdAndName(organizationId, ORGANIZATION_ADMIN_ROLE)
                .orElseGet(() -> ensureOrganizationAdminRoleEntity(organizationId));
    }

    private Role ensureOrganizationAdminRoleEntity(UUID organizationId) {
        return roleRepository.findByOrganizationIdAndName(organizationId, ORGANIZATION_ADMIN_ROLE)
                .orElseGet(() -> {
                    Role role = new Role(ORGANIZATION_ADMIN_ROLE, "Organization administrator", organizationId, false);
                    role.replacePermissions(resolvePermissions(Set.copyOf(ORGANIZATION_ADMIN_PERMISSIONS)));
                    return roleRepository.save(role);
                });
    }

    private Role findVisibleRole(UUID id) {
        if (CurrentUser.required().isPlatformUser()) {
            return roleRepository.findById(id)
                    .orElseThrow(() -> notFound("ROLE_NOT_FOUND", "Role not found"));
        }
        return roleRepository.findByIdAndOrganizationId(id, CurrentUser.requiredOrganizationId())
                .orElseThrow(() -> notFound("ROLE_NOT_FOUND", "Role not found"));
    }

    private Set<Permission> resolvePermissions(Set<String> codes) {
        Set<String> normalizedCodes = codes.stream()
                .map(code -> code.trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
        List<Permission> permissions = permissionRepository.findAllByCodeIn(normalizedCodes);
        if (permissions.size() != normalizedCodes.size()) {
            Set<String> found = permissions.stream().map(Permission::getCode).collect(Collectors.toSet());
            Set<String> unknown = normalizedCodes.stream().filter(code -> !found.contains(code)).collect(Collectors.toSet());
            throw new BusinessException("UNKNOWN_PERMISSION", "Unknown permission codes: " + String.join(", ", unknown), HttpStatus.BAD_REQUEST);
        }
        return Set.copyOf(permissions);
    }

    private void verifyOrganizationScope(UUID organizationId) {
        if (!CurrentUser.required().isPlatformUser() && !organizationId.equals(CurrentUser.organizationId())) {
            throw notFound("ORGANIZATION_NOT_FOUND", "Organization not found");
        }
    }

    private void requirePlatformUser() {
        if (!CurrentUser.required().isPlatformUser()) {
            throw forbidden();
        }
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getOrganizationId(),
                role.isPlatformRole(),
                role.getPermissions().stream().map(Permission::getCode).sorted().toList()
        );
    }

    private BusinessException notFound(String code, String message) {
        return new BusinessException(code, message, HttpStatus.NOT_FOUND);
    }

    private BusinessException forbidden() {
        return new BusinessException("ACCESS_DENIED", "You are not allowed to assign this role", HttpStatus.FORBIDDEN);
    }
}

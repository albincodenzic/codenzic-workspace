package com.codenzic.workspace.organization.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.identity.application.RoleService;
import com.codenzic.workspace.organization.domain.Organization;
import com.codenzic.workspace.organization.domain.OrganizationStatus;
import com.codenzic.workspace.organization.infrastructure.OrganizationRepository;
import com.codenzic.workspace.organization.presentation.dto.OrganizationRequest;
import com.codenzic.workspace.organization.presentation.dto.OrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final RoleService roleService;
    private final AuditLogService audit;

    @Transactional
    public OrganizationResponse create(OrganizationRequest request) {
        if (!CurrentUser.required().isPlatformUser()) {
            throw new BusinessException("PLATFORM_SCOPE_REQUIRED", "Only platform users can create organizations", HttpStatus.FORBIDDEN);
        }
        String slug = request.slug().trim().toLowerCase();
        if (organizationRepository.existsBySlugIgnoreCase(slug)) {
            throw new BusinessException("SLUG_EXISTS", "Organization slug already exists", HttpStatus.CONFLICT);
        }
        Organization organization = organizationRepository.save(
                new Organization(request.name().trim(), slug, request.description()));
        roleService.ensureOrganizationDefaultRoles(organization.getId());
        audit.record(organization.getId(), CurrentUser.id(), "CREATE", "ORGANIZATION", organization.getId(), "{}");
        return toResponse(organization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> list() {
        if (CurrentUser.required().isPlatformUser()) {
            return organizationRepository.findAll().stream().map(this::toResponse).toList();
        }
        UUID organizationId = CurrentUser.requiredOrganizationId();
        return organizationRepository.findById(organizationId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrganizationResponse get(UUID id) {
        verifyScope(id);
        return toResponse(find(id));
    }

    @Transactional
    public OrganizationResponse update(UUID id, OrganizationRequest request) {
        requirePlatformUser();
        verifyScope(id);
        Organization organization = find(id);
        String slug = request.slug().trim().toLowerCase();
        if (organizationRepository.existsBySlugIgnoreCaseAndIdNot(slug, id)) {
            throw new BusinessException("SLUG_EXISTS", "Organization slug already exists", HttpStatus.CONFLICT);
        }
        organization.update(request.name().trim(), slug, request.description());
        audit.record(organization.getId(), CurrentUser.id(), "UPDATE", "ORGANIZATION", organization.getId(), "{}");
        return toResponse(organization);
    }

    @Transactional
    public OrganizationResponse updateStatus(UUID id, OrganizationStatus status) {
        requirePlatformUser();
        verifyScope(id);
        Organization organization = find(id);
        organization.updateStatus(status);
        audit.record(organization.getId(), CurrentUser.id(), "STATUS_CHANGE", "ORGANIZATION", organization.getId(),
                "{\"status\":\"" + status.name() + "\"}");
        return toResponse(organization);
    }

    private void verifyScope(UUID id) {
        if (!CurrentUser.required().isPlatformUser() && !id.equals(CurrentUser.organizationId())) {
            throw new BusinessException("ORGANIZATION_NOT_FOUND", "Organization not found", HttpStatus.NOT_FOUND);
        }
    }

    private void requirePlatformUser() {
        if (!CurrentUser.required().isPlatformUser()) {
            throw new BusinessException("PLATFORM_SCOPE_REQUIRED", "Only platform users can manage organizations", HttpStatus.FORBIDDEN);
        }
    }

    private Organization find(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ORGANIZATION_NOT_FOUND", "Organization not found", HttpStatus.NOT_FOUND));
    }

    private OrganizationResponse toResponse(Organization organization) {
        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                organization.getDescription(),
                organization.getStatus()
        );
    }
}

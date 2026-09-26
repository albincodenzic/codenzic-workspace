package com.codenzic.workspace.organization.infrastructure;

import com.codenzic.workspace.organization.domain.Organization;
import com.codenzic.workspace.organization.domain.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    boolean existsBySlugIgnoreCase(String slug);
    boolean existsBySlugIgnoreCaseAndIdNot(String slug, UUID id);
    boolean existsByIdAndStatus(UUID id, OrganizationStatus status);
    Optional<Organization> findBySlugIgnoreCase(String slug);
}
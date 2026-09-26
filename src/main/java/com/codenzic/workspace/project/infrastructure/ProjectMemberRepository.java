package com.codenzic.workspace.project.infrastructure;

import com.codenzic.workspace.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
    List<ProjectMember> findAllByProjectIdAndOrganizationIdOrderByCreatedAt(UUID projectId, UUID organizationId);

    Optional<ProjectMember> findByProjectIdAndEmployeeIdAndOrganizationId(
            UUID projectId,
            UUID employeeId,
            UUID organizationId
    );

    boolean existsByProjectIdAndEmployeeIdAndOrganizationId(UUID projectId, UUID employeeId, UUID organizationId);
}

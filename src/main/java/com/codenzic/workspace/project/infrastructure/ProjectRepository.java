package com.codenzic.workspace.project.infrastructure;

import com.codenzic.workspace.project.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    boolean existsByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<Project> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query(
            value = """
                    select project from Project project
                    where project.organizationId = :organizationId
                      and (:search is null or lower(project.name) like :search
                           or lower(coalesce(project.description, '')) like :search)
                      and (:status is null or project.status = :status)
                      and (:employeeId is null or project.managerId = :employeeId or exists (
                           select member.id from ProjectMember member
                           where member.projectId = project.id and member.employeeId = :employeeId
                             and member.organizationId = :organizationId))
                    """,
            countQuery = """
                    select count(project) from Project project
                    where project.organizationId = :organizationId
                      and (:search is null or lower(project.name) like :search
                           or lower(coalesce(project.description, '')) like :search)
                      and (:status is null or project.status = :status)
                      and (:employeeId is null or project.managerId = :employeeId or exists (
                           select member.id from ProjectMember member
                           where member.projectId = project.id and member.employeeId = :employeeId
                             and member.organizationId = :organizationId))
                    """
    )
    Page<Project> search(
            @Param("organizationId") UUID organizationId,
            @Param("search") String search,
            @Param("status") String status,
            @Param("employeeId") UUID employeeId,
            Pageable pageable
    );
}
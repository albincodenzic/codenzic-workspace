package com.codenzic.workspace.employee.infrastructure;

import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.domain.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findAllByOrganizationId(UUID organizationId);

    List<Employee> findAllByOrganizationIdAndIdIn(UUID organizationId, List<UUID> ids);

    boolean existsByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    Optional<Employee> findByIdAndOrganizationId(UUID id, UUID organizationId);

    Optional<Employee> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);

    @Query(
            value = """
                    select e from Employee e
                    join User u on u.id = e.userId
                    where e.organizationId = :organizationId
                      and (:search is null or lower(u.email) like :search
                           or lower(u.firstName) like :search
                           or lower(u.lastName) like :search
                           or lower(coalesce(e.jobTitle, '')) like :search)
                      and (:status is null or e.status = :status)
                      and (:teamId is null or exists (
                           select member.id from TeamMember member
                           where member.employeeId = e.id and member.teamId = :teamId
                             and member.organizationId = :organizationId))
                      and (:departmentId is null or exists (
                           select member.id from TeamMember member join Team team on team.id = member.teamId
                           where member.employeeId = e.id and team.departmentId = :departmentId
                             and member.organizationId = :organizationId and team.organizationId = :organizationId))
                      and (:role is null or exists (
                           select assignedRole.id from User roleUser join roleUser.roles assignedRole
                           where roleUser.id = e.userId and lower(assignedRole.name) = lower(:role)))
                    """,
            countQuery = """
                    select count(e) from Employee e
                    join User u on u.id = e.userId
                    where e.organizationId = :organizationId
                      and (:search is null or lower(u.email) like :search
                           or lower(u.firstName) like :search
                           or lower(u.lastName) like :search
                           or lower(coalesce(e.jobTitle, '')) like :search)
                      and (:status is null or e.status = :status)
                      and (:teamId is null or exists (
                           select member.id from TeamMember member
                           where member.employeeId = e.id and member.teamId = :teamId
                             and member.organizationId = :organizationId))
                      and (:departmentId is null or exists (
                           select member.id from TeamMember member join Team team on team.id = member.teamId
                           where member.employeeId = e.id and team.departmentId = :departmentId
                             and member.organizationId = :organizationId and team.organizationId = :organizationId))
                      and (:role is null or exists (
                           select assignedRole.id from User roleUser join roleUser.roles assignedRole
                           where roleUser.id = e.userId and lower(assignedRole.name) = lower(:role)))
                    """
    )
    Page<Employee> search(
            @Param("organizationId") UUID organizationId,
            @Param("search") String search,
            @Param("status") EmployeeStatus status,
            @Param("departmentId") UUID departmentId,
            @Param("teamId") UUID teamId,
            @Param("role") String role,
            Pageable pageable
    );
}
package com.codenzic.workspace.task.infrastructure;

import com.codenzic.workspace.task.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    Optional<Task> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @Query(
            value = """
                    select task from Task task
                    where task.organizationId = :organizationId
                      and (:projectId is null or task.projectId = :projectId)
                      and (:employeeId is null or task.assigneeId = :employeeId)
                      and (:teamScoped = false or task.teamId in :teamIds)
                      and (:status is null or task.status = :status)
                      and (:priority is null or task.priority = :priority)
                      and (:search is null or lower(task.title) like :search
                           or lower(coalesce(task.description, '')) like :search)
                    """,
            countQuery = """
                    select count(task) from Task task
                    where task.organizationId = :organizationId
                      and (:projectId is null or task.projectId = :projectId)
                      and (:employeeId is null or task.assigneeId = :employeeId)
                      and (:teamScoped = false or task.teamId in :teamIds)
                      and (:status is null or task.status = :status)
                      and (:priority is null or task.priority = :priority)
                      and (:search is null or lower(task.title) like :search
                           or lower(coalesce(task.description, '')) like :search)
                    """
    )
    Page<Task> search(
            @Param("organizationId") UUID organizationId,
            @Param("projectId") UUID projectId,
            @Param("employeeId") UUID employeeId,
            @Param("teamScoped") boolean teamScoped,
            @Param("teamIds") List<UUID> teamIds,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("search") String search,
            Pageable pageable
    );
}
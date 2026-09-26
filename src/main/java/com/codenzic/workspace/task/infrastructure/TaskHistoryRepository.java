package com.codenzic.workspace.task.infrastructure;

import com.codenzic.workspace.task.domain.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, UUID> {
    List<TaskHistory> findAllByTaskIdAndOrganizationIdOrderByCreatedAtDesc(UUID taskId, UUID organizationId);
}

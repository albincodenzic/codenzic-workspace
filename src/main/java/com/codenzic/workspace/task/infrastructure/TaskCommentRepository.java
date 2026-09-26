package com.codenzic.workspace.task.infrastructure;

import com.codenzic.workspace.task.domain.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskCommentRepository extends JpaRepository<TaskComment, UUID> {
    List<TaskComment> findAllByTaskIdAndOrganizationIdOrderByCreatedAtAsc(UUID taskId, UUID organizationId);
}

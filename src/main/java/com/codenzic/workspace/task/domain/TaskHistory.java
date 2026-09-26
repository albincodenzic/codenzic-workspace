package com.codenzic.workspace.task.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "task_history")
@Getter
@NoArgsConstructor
public class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private UUID taskId;

    @Column(nullable = false)
    private UUID actorId;

    @Column(nullable = false, length = 80)
    private String fieldName;

    @Column(length = 2000)
    private String oldValue;

    @Column(length = 2000)
    private String newValue;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public TaskHistory(UUID organizationId, UUID taskId, UUID actorId, String fieldName, String oldValue, String newValue) {
        this.organizationId = organizationId;
        this.taskId = taskId;
        this.actorId = actorId;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}

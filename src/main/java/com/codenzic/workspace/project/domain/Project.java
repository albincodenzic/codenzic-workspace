package com.codenzic.workspace.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE";

    private LocalDate startDate;

    private LocalDate dueDate;

    private UUID managerId;

    @Column(nullable = false)
    private int progress;

    public Project(
            UUID organizationId,
            String name,
            String description,
            String status,
            LocalDate startDate,
            LocalDate dueDate,
            UUID managerId,
            int progress
    ) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.managerId = managerId;
        this.progress = progress;
    }

    public void update(
            String name,
            String description,
            LocalDate startDate,
            LocalDate dueDate,
            UUID managerId,
            int progress
    ) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.managerId = managerId;
        this.progress = progress;
    }

    public void changeStatus(String status) {
        this.status = status;
    }
}

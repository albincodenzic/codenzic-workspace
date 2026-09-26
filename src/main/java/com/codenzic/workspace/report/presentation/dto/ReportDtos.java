package com.codenzic.workspace.report.presentation.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class ReportDtos {
    private ReportDtos() {
    }

    public record Attendance(LocalDate date, String status, long count) {
    }

    public record Leave(String type, String status, long count) {
    }

    public record Employee(String status, long count) {
    }

    public record Project(UUID id, String name, String status, int progress, long taskCount) {
    }

    public record Task(String status, String priority, long count) {
    }

    public record Eod(String status, long count) {
    }

    public record Response(
            List<Attendance> attendance,
            List<Leave> leave,
            List<Employee> employees,
            List<Project> projects,
            List<Task> tasks,
            List<Eod> eod
    ) {
    }
}

package com.codenzic.workspace.report.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "Report Dtos payload.")
public final class ReportDtos {
    private ReportDtos() {
    }

@Schema(description = "Attendance payload.")
    public record Attendance(LocalDate date, String status, long count) {
    }

@Schema(description = "Leave payload.")
    public record Leave(String type, String status, long count) {
    }

@Schema(description = "Employee payload.")
    public record Employee(String status, long count) {
    }

@Schema(description = "Project payload.")
    public record Project(UUID id, String name, String status, int progress, long taskCount) {
    }

@Schema(description = "Task payload.")
    public record Task(String status, String priority, long count) {
    }

@Schema(description = "Eod payload.")
    public record Eod(String status, long count) {
    }

@Schema(description = "Response payload.")
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

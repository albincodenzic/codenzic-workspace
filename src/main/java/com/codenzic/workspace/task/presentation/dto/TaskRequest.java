package com.codenzic.workspace.task.presentation.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
public record TaskRequest(@NotNull UUID projectId, UUID teamId, UUID assigneeId, @NotBlank @Size(max=250) String title,
 @Size(max=2000) String description, @Pattern(regexp="TODO|IN_PROGRESS|IN_REVIEW|COMPLETED|BLOCKED|CANCELLED") String status,
 @Pattern(regexp="LOW|MEDIUM|HIGH|URGENT") String priority, LocalDate dueDate) {}
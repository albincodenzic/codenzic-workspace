package com.codenzic.workspace.eod.presentation.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;
public record EodRequest(UUID employeeId,@NotNull LocalDate reportDate,@NotBlank @Size(max=3000) String summary,@Size(max=2000) String blockers) {}

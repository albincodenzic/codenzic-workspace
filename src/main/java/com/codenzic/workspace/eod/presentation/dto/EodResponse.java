package com.codenzic.workspace.eod.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema; import java.time.*; import java.util.UUID; @Schema(description = "Eod Response payload.") public record EodResponse(UUID id,UUID organizationId,UUID employeeId,LocalDate reportDate,String summary,String blockers,String status,Instant submittedAt,UUID reviewedBy,Instant reviewedAt,String reviewComment) {}

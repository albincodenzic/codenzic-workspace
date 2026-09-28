package com.codenzic.workspace.company.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*; import java.time.Instant; import java.util.UUID;
@Schema(description = "Company Settings Dto payload.")
public record CompanySettingsDto(UUID organizationId,String timezone,String currency,String workWeek,String logoUrl,Instant updatedAt) {
@Schema(description = "Request payload.")
 public record Request(@NotBlank @Size(max=80) String timezone,@NotBlank @Size(min=3,max=3) String currency,@NotBlank String workWeek,@Size(max=500) String logoUrl) {}
}

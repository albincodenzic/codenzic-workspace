package com.codenzic.workspace.company.presentation.dto;
import jakarta.validation.constraints.*; import java.time.Instant; import java.util.UUID;
public record CompanySettingsDto(UUID organizationId,String timezone,String currency,String workWeek,String logoUrl,Instant updatedAt) {
 public record Request(@NotBlank @Size(max=80) String timezone,@NotBlank @Size(min=3,max=3) String currency,@NotBlank String workWeek,@Size(max=500) String logoUrl) {}
}

package com.codenzic.workspace.communication.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*; import com.codenzic.workspace.communication.domain.Announcement;

@Schema(description = "Communication Dtos payload.")
public final class CommunicationDtos { private CommunicationDtos() {}
@Schema(description = "Conversation Request payload.")
 public record ConversationRequest(@Size(max=200) String title, @NotEmpty Set<UUID> participantIds) {}
@Schema(description = "Conversation Response payload.")
 public record ConversationResponse(UUID id, UUID organizationId, String title, UUID createdBy, Set<UUID> participantIds, Instant createdAt, Instant lastReadAt, long unreadMessageCount) {}
@Schema(description = "Message Request payload.")
 public record MessageRequest(@NotBlank @Size(max=4000) String body) {}
@Schema(description = "Message Response payload.")
 public record MessageResponse(UUID id, UUID conversationId, UUID senderId, String body, Instant createdAt) {}
@Schema(description = "Announcement Request payload.")
 public record AnnouncementRequest(@NotNull Announcement.Scope scope, @NotBlank @Size(max=200) String title, @NotBlank @Size(max=10000) String content, UUID teamId, UUID departmentId) {}
@Schema(description = "Announcement Response payload.")
 public record AnnouncementResponse(UUID id, UUID organizationId, Announcement.Scope scope, String title, String content, UUID createdBy, String status, UUID teamId, UUID departmentId, Instant publishedAt) {}
@Schema(description = "Notification Response payload.")
 public record NotificationResponse(UUID id, UUID userId, UUID organizationId, String type, String title, String body, UUID announcementId, Instant readAt, Instant createdAt) {}
}

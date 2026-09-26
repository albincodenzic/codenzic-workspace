package com.codenzic.workspace.communication.presentation.dto;
import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*; import com.codenzic.workspace.communication.domain.Announcement;

public final class CommunicationDtos { private CommunicationDtos() {}
 public record ConversationRequest(@Size(max=200) String title, @NotEmpty Set<UUID> participantIds) {}
 public record ConversationResponse(UUID id, UUID organizationId, String title, UUID createdBy, Set<UUID> participantIds, Instant createdAt, Instant lastReadAt, long unreadMessageCount) {}
 public record MessageRequest(@NotBlank @Size(max=4000) String body) {}
 public record MessageResponse(UUID id, UUID conversationId, UUID senderId, String body, Instant createdAt) {}
 public record AnnouncementRequest(@NotNull Announcement.Scope scope, @NotBlank @Size(max=200) String title, @NotBlank @Size(max=10000) String content, UUID teamId, UUID departmentId) {}
 public record AnnouncementResponse(UUID id, UUID organizationId, Announcement.Scope scope, String title, String content, UUID createdBy, String status, UUID teamId, UUID departmentId, Instant publishedAt) {}
 public record NotificationResponse(UUID id, UUID userId, UUID organizationId, String type, String title, String body, UUID announcementId, Instant readAt, Instant createdAt) {}
}

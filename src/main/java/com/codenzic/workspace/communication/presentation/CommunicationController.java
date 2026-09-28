package com.codenzic.workspace.communication.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.communication.application.AnnouncementService;
import com.codenzic.workspace.communication.application.ConversationService;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.AnnouncementRequest;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.AnnouncementResponse;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.ConversationRequest;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.ConversationResponse;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.MessageRequest;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.MessageResponse;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.NotificationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommunicationController {
    private final ConversationService conversations;
    private final AnnouncementService announcements;
    private final NotificationService notifications;

    @Tag(name = "Chat", description = "Chat API operations.")
    @Operation(summary = "Create Conversation", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/api/v1/conversations")
    @PreAuthorize("hasAuthority('CHAT_CREATE')")
    public ApiResponse<ConversationResponse> createConversation(@Valid @RequestBody ConversationRequest request) {
        return ApiResponse.success(conversations.create(request), "/api/v1/conversations");
    }

    @Tag(name = "Chat", description = "Chat API operations.")
    @Operation(summary = "Conversations", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/conversations")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<PageResponse<ConversationResponse>> conversations(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(conversations.list(pageable), "/api/v1/conversations");
    }

    @Tag(name = "Chat", description = "Chat API operations.")
    @Operation(summary = "Conversation", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/conversations/{id}")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<ConversationResponse> conversation(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(conversations.get(id), "/api/v1/conversations/" + id);
    }

    @Tag(name = "Chat", description = "Chat API operations.")
    @Operation(summary = "Messages", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/conversations/{id}/messages")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<PageResponse<MessageResponse>> messages(
            @Parameter(description = "Resource identifier.") @PathVariable UUID id,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.success(conversations.messages(id, pageable), "/api/v1/conversations/" + id + "/messages");
    }

    @Tag(name = "Chat", description = "Chat API operations.")
    @Operation(summary = "Send", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/api/v1/conversations/{id}/messages")
    @PreAuthorize("hasAuthority('CHAT_SEND')")
    public ApiResponse<MessageResponse> send(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody MessageRequest request) {
        return ApiResponse.success(conversations.send(id, request), "/api/v1/conversations/" + id + "/messages");
    }

    @Tag(name = "Chat", description = "Chat API operations.")
    @Operation(summary = "Mark Conversation Read", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/api/v1/conversations/{id}/read")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<ConversationResponse> markConversationRead(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(conversations.markRead(id), "/api/v1/conversations/" + id + "/read");
    }

    @Tag(name = "Announcements", description = "Announcements API operations.")
    @Operation(summary = "Create Announcement", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/api/v1/announcements")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_CREATE')")
    public ApiResponse<AnnouncementResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return ApiResponse.success(announcements.create(request), "/api/v1/announcements");
    }

    @Tag(name = "Announcements", description = "Announcements API operations.")
    @Operation(summary = "Announcements", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/announcements")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_READ')")
    public ApiResponse<List<AnnouncementResponse>> announcements() {
        return ApiResponse.success(announcements.list(), "/api/v1/announcements");
    }

    @Tag(name = "Announcements", description = "Announcements API operations.")
    @Operation(summary = "Announcement", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/announcements/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_READ')")
    public ApiResponse<AnnouncementResponse> announcement(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(announcements.get(id), "/api/v1/announcements/" + id);
    }

    @Tag(name = "Announcements", description = "Announcements API operations.")
    @Operation(summary = "Update Announcement", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PutMapping("/api/v1/announcements/{id}")
    @PreAuthorize("hasAnyAuthority('ANNOUNCEMENT_UPDATE','ANNOUNCEMENT_CREATE')")
    public ApiResponse<AnnouncementResponse> updateAnnouncement(
            @Parameter(description = "Resource identifier.") @PathVariable UUID id,
            @Valid @RequestBody AnnouncementRequest request
    ) {
        return ApiResponse.success(announcements.update(id, request), "/api/v1/announcements/" + id);
    }

    @Tag(name = "Announcements", description = "Announcements API operations.")
    @Operation(summary = "Publish Announcement", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/api/v1/announcements/{id}/publish")
    @PreAuthorize("hasAnyAuthority('ANNOUNCEMENT_PUBLISH','ANNOUNCEMENT_CREATE')")
    public ApiResponse<AnnouncementResponse> publishAnnouncement(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(announcements.publish(id), "/api/v1/announcements/" + id + "/publish");
    }

    @Tag(name = "Announcements", description = "Announcements API operations.")
    @Operation(summary = "Archive Announcement", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/api/v1/announcements/{id}/archive")
    @PreAuthorize("hasAnyAuthority('ANNOUNCEMENT_UPDATE','ANNOUNCEMENT_PUBLISH')")
    public ApiResponse<AnnouncementResponse> archiveAnnouncement(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(announcements.archive(id), "/api/v1/announcements/" + id + "/archive");
    }

    @Tag(name = "Notifications", description = "Notifications API operations.")
    @Operation(summary = "Notifications", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/notifications")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<PageResponse<NotificationResponse>> notifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(notifications.list(pageable), "/api/v1/notifications");
    }

    @Tag(name = "Notifications", description = "Notifications API operations.")
    @Operation(summary = "Unread Notifications", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/notifications/unread")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<PageResponse<NotificationResponse>> unreadNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(notifications.unread(pageable), "/api/v1/notifications/unread");
    }

    @Tag(name = "Notifications", description = "Notifications API operations.")
    @Operation(summary = "Unread Notification Count", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/api/v1/notifications/unread-count")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<Long> unreadNotificationCount() {
        return ApiResponse.success(notifications.unreadCount(), "/api/v1/notifications/unread-count");
    }

    @Tag(name = "Notifications", description = "Notifications API operations.")
    @Operation(summary = "Read Notification", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/api/v1/notifications/{id}/read")
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public ApiResponse<NotificationResponse> readNotification(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(notifications.markRead(id), "/api/v1/notifications/" + id + "/read");
    }

    @Tag(name = "Notifications", description = "Notifications API operations.")
    @Operation(summary = "Read All Notifications", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/api/v1/notifications/read-all")
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public ApiResponse<Integer> readAllNotifications() {
        return ApiResponse.success(notifications.markAllRead(), "/api/v1/notifications/read-all");
    }
}

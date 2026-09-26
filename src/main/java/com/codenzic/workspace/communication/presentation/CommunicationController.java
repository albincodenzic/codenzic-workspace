package com.codenzic.workspace.communication.presentation;

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
public class CommunicationController {
    private final ConversationService conversations;
    private final AnnouncementService announcements;
    private final NotificationService notifications;

    @PostMapping("/api/v1/conversations")
    @PreAuthorize("hasAuthority('CHAT_CREATE')")
    public ApiResponse<ConversationResponse> createConversation(@Valid @RequestBody ConversationRequest request) {
        return ApiResponse.success(conversations.create(request), "/api/v1/conversations");
    }

    @GetMapping("/api/v1/conversations")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<PageResponse<ConversationResponse>> conversations(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(conversations.list(pageable), "/api/v1/conversations");
    }

    @GetMapping("/api/v1/conversations/{id}")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<ConversationResponse> conversation(@PathVariable UUID id) {
        return ApiResponse.success(conversations.get(id), "/api/v1/conversations/" + id);
    }

    @GetMapping("/api/v1/conversations/{id}/messages")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<PageResponse<MessageResponse>> messages(
            @PathVariable UUID id,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.success(conversations.messages(id, pageable), "/api/v1/conversations/" + id + "/messages");
    }

    @PostMapping("/api/v1/conversations/{id}/messages")
    @PreAuthorize("hasAuthority('CHAT_SEND')")
    public ApiResponse<MessageResponse> send(@PathVariable UUID id, @Valid @RequestBody MessageRequest request) {
        return ApiResponse.success(conversations.send(id, request), "/api/v1/conversations/" + id + "/messages");
    }

    @PatchMapping("/api/v1/conversations/{id}/read")
    @PreAuthorize("hasAuthority('CHAT_READ')")
    public ApiResponse<ConversationResponse> markConversationRead(@PathVariable UUID id) {
        return ApiResponse.success(conversations.markRead(id), "/api/v1/conversations/" + id + "/read");
    }

    @PostMapping("/api/v1/announcements")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_CREATE')")
    public ApiResponse<AnnouncementResponse> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return ApiResponse.success(announcements.create(request), "/api/v1/announcements");
    }

    @GetMapping("/api/v1/announcements")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_READ')")
    public ApiResponse<List<AnnouncementResponse>> announcements() {
        return ApiResponse.success(announcements.list(), "/api/v1/announcements");
    }

    @GetMapping("/api/v1/announcements/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_READ')")
    public ApiResponse<AnnouncementResponse> announcement(@PathVariable UUID id) {
        return ApiResponse.success(announcements.get(id), "/api/v1/announcements/" + id);
    }

    @PutMapping("/api/v1/announcements/{id}")
    @PreAuthorize("hasAnyAuthority('ANNOUNCEMENT_UPDATE','ANNOUNCEMENT_CREATE')")
    public ApiResponse<AnnouncementResponse> updateAnnouncement(
            @PathVariable UUID id,
            @Valid @RequestBody AnnouncementRequest request
    ) {
        return ApiResponse.success(announcements.update(id, request), "/api/v1/announcements/" + id);
    }

    @PatchMapping("/api/v1/announcements/{id}/publish")
    @PreAuthorize("hasAnyAuthority('ANNOUNCEMENT_PUBLISH','ANNOUNCEMENT_CREATE')")
    public ApiResponse<AnnouncementResponse> publishAnnouncement(@PathVariable UUID id) {
        return ApiResponse.success(announcements.publish(id), "/api/v1/announcements/" + id + "/publish");
    }

    @PatchMapping("/api/v1/announcements/{id}/archive")
    @PreAuthorize("hasAnyAuthority('ANNOUNCEMENT_UPDATE','ANNOUNCEMENT_PUBLISH')")
    public ApiResponse<AnnouncementResponse> archiveAnnouncement(@PathVariable UUID id) {
        return ApiResponse.success(announcements.archive(id), "/api/v1/announcements/" + id + "/archive");
    }

    @GetMapping("/api/v1/notifications")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<PageResponse<NotificationResponse>> notifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(notifications.list(pageable), "/api/v1/notifications");
    }

    @GetMapping("/api/v1/notifications/unread")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<PageResponse<NotificationResponse>> unreadNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(notifications.unread(pageable), "/api/v1/notifications/unread");
    }

    @GetMapping("/api/v1/notifications/unread-count")
    @PreAuthorize("hasAuthority('NOTIFICATION_READ')")
    public ApiResponse<Long> unreadNotificationCount() {
        return ApiResponse.success(notifications.unreadCount(), "/api/v1/notifications/unread-count");
    }

    @PatchMapping("/api/v1/notifications/{id}/read")
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public ApiResponse<NotificationResponse> readNotification(@PathVariable UUID id) {
        return ApiResponse.success(notifications.markRead(id), "/api/v1/notifications/" + id + "/read");
    }

    @PatchMapping("/api/v1/notifications/read-all")
    @PreAuthorize("hasAuthority('NOTIFICATION_UPDATE')")
    public ApiResponse<Integer> readAllNotifications() {
        return ApiResponse.success(notifications.markAllRead(), "/api/v1/notifications/read-all");
    }
}

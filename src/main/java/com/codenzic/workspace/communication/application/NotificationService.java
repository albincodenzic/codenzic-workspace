package com.codenzic.workspace.communication.application;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.communication.domain.Notification;
import com.codenzic.workspace.communication.infrastructure.NotificationRepository;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.NotificationResponse;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    private final UserRepository users;

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(Pageable pageable) {
        return PageResponse.from(repository.findAllByUserIdOrderByCreatedAtDesc(CurrentUser.id(), pageable).map(this::to));
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> unread(Pageable pageable) {
        return PageResponse.from(repository.findAllByUserIdAndReadAtIsNullOrderByCreatedAtDesc(CurrentUser.id(), pageable)
                .map(this::to));
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return repository.countByUserIdAndReadAtIsNull(CurrentUser.id());
    }

    @Transactional
    public void notifyUser(UUID organizationId, UUID userId, String type, String title, String body) {
        if (organizationId == null || !users.existsByIdAndOrganizationId(userId, organizationId)) {
            throw new BusinessException("NOTIFICATION_RECIPIENT_SCOPE",
                    "Notification recipient must belong to the target organization", HttpStatus.BAD_REQUEST);
        }
        repository.save(new Notification(userId, organizationId, type, title, body, null));
    }

    @Transactional
    public NotificationResponse markRead(UUID id) {
        Notification notification = repository.findByIdAndUserId(id, CurrentUser.id())
                .orElseThrow(() -> new BusinessException("NOTIFICATION_NOT_FOUND", "Notification not found", HttpStatus.NOT_FOUND));
        notification.markRead();
        return to(notification);
    }

    @Transactional
    public int markAllRead() {
        return repository.markAllRead(CurrentUser.id());
    }

    private NotificationResponse to(Notification notification) {
        return new NotificationResponse(notification.getId(), notification.getUserId(), notification.getOrganizationId(),
                notification.getType(), notification.getTitle(), notification.getBody(), notification.getAnnouncementId(),
                notification.getReadAt(), notification.getCreatedAt());
    }
}

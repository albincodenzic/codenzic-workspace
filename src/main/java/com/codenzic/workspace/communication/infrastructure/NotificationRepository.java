package com.codenzic.workspace.communication.infrastructure;
import com.codenzic.workspace.communication.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,UUID> {
    Page<Notification> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<Notification> findAllByUserIdAndReadAtIsNullOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Optional<Notification> findByIdAndUserId(UUID id, UUID userId);
    long countByUserIdAndReadAtIsNull(UUID userId);

    @Modifying
    @Query("update Notification notification set notification.readAt = CURRENT_TIMESTAMP where notification.userId = :userId and notification.readAt is null")
    int markAllRead(@Param("userId") UUID userId);
}

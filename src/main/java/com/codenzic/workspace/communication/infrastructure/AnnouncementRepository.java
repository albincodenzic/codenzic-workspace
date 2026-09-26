package com.codenzic.workspace.communication.infrastructure;
import com.codenzic.workspace.communication.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface AnnouncementRepository extends JpaRepository<Announcement,UUID> {
    List<Announcement> findAllByScopeOrOrganizationIdOrderByPublishedAtDesc(Announcement.Scope scope, UUID organizationId);
    Optional<Announcement> findByIdAndOrganizationId(UUID id, UUID organizationId);
    @Query("""
            select announcement from Announcement announcement
            where announcement.id = :id
              and (announcement.organizationId = :organizationId
                   or (announcement.scope = :platformScope and announcement.organizationId is null))
            """)
    Optional<Announcement> findVisibleById(
            @Param("id") UUID id,
            @Param("organizationId") UUID organizationId,
            @Param("platformScope") Announcement.Scope platformScope
    );
    List<Announcement> findAllByScopeAndStatusOrderByPublishedAtDesc(Announcement.Scope scope, String status);
    List<Announcement> findAllByStatusOrderByPublishedAtDesc(String status);
    List<Announcement> findAllByOrganizationIdAndStatusOrderByPublishedAtDesc(UUID organizationId, String status);
    List<Announcement> findAllByOrganizationIdAndCreatedByAndStatusOrderByPublishedAtDesc(
            UUID organizationId, UUID createdBy, String status);
}

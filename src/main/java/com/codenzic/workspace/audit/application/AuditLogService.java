package com.codenzic.workspace.audit.application;

import com.codenzic.workspace.audit.domain.AuditLog;
import com.codenzic.workspace.audit.infrastructure.AuditLogRepository;
import com.codenzic.workspace.audit.presentation.dto.AuditLogResponse;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository repository;

    @Transactional
    public void record(String action, String entityType, UUID entityId, String metadata) {
        record(CurrentUser.organizationId(), CurrentUser.id(), action, entityType, entityId, metadata);
    }

    @Transactional
    public void record(
            UUID organizationId,
            UUID actorId,
            String action,
            String entityType,
            UUID entityId,
            String metadata
    ) {
        repository.save(new AuditLog(organizationId, actorId, action, entityType, entityId, metadata));
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> list(int limit, UUID requestedOrganizationId) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        UUID organizationId = CurrentUser.organizationId();
        List<AuditLog> logs;
        if (CurrentUser.required().isPlatformUser()) {
            organizationId = requestedOrganizationId;
            logs = organizationId == null
                    ? repository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, safeLimit))
                    : repository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId, PageRequest.of(0, safeLimit));
        } else {
            if (requestedOrganizationId != null && !requestedOrganizationId.equals(organizationId)) {
                throw new BusinessException("ORGANIZATION_ACCESS_DENIED",
                        "You do not have access to this organisation", HttpStatus.FORBIDDEN);
            }
            logs = repository.findAllByOrganizationIdOrderByCreatedAtDesc(organizationId, PageRequest.of(0, safeLimit));
        }
        return logs.stream().map(log -> new AuditLogResponse(log.getId(), log.getActorId(),
                log.getOrganizationId(), log.getAction(), log.getResourceType(), log.getResourceId(),
                log.getCreatedAt(), log.getDetails())).toList();
    }
}

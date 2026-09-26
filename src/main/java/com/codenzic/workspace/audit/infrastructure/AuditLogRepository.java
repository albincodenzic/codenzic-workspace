package com.codenzic.workspace.audit.infrastructure;
import com.codenzic.workspace.audit.domain.AuditLog; import org.springframework.data.domain.Pageable; import org.springframework.data.jpa.repository.*; import java.util.*; 
public interface AuditLogRepository extends JpaRepository<AuditLog,UUID> {
 List<AuditLog> findAllByOrganizationIdOrderByCreatedAtDesc(UUID organizationId,Pageable pageable);
 List<AuditLog> findAllByOrganizationIdIsNullOrderByCreatedAtDesc(Pageable pageable);
 List<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}

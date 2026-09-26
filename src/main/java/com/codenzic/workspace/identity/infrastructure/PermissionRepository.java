package com.codenzic.workspace.identity.infrastructure;

import com.codenzic.workspace.identity.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    List<Permission> findAllByOrderByCodeAsc();
    Optional<Permission> findByCodeIgnoreCase(String code);
    List<Permission> findAllByCodeIn(Set<String> codes);
}

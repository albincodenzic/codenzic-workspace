package com.codenzic.workspace.identity.infrastructure;

import com.codenzic.workspace.identity.domain.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    @EntityGraph(attributePaths = "permissions")
    @Query("select distinct role from Role role")
    List<Role> findAllWithPermissions();

    @EntityGraph(attributePaths = "permissions")
    List<Role> findAllByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = "permissions")
    List<Role> findAllByOrganizationIdIsNullAndPlatformRoleTrue();

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findByIdAndOrganizationId(UUID id, UUID organizationId);

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findByIdAndPlatformRoleTrueAndOrganizationIdIsNull(UUID id);

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findByOrganizationIdAndName(UUID organizationId, String name);

    boolean existsByOrganizationIdAndNameIgnoreCase(UUID organizationId, String name);
    boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(UUID organizationId, String name, UUID id);

    @Modifying
    @Query(value = "delete from user_roles where role_id = :roleId", nativeQuery = true)
    void deleteUserAssignments(@Param("roleId") UUID roleId);

    @Modifying
    @Query(value = "delete from role_permissions where role_id = :roleId", nativeQuery = true)
    void deleteRolePermissions(@Param("roleId") UUID roleId);

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findByNameAndPlatformRoleTrueAndOrganizationIdIsNull(String name);
}

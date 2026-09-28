package com.codenzic.workspace.identity.infrastructure;

import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.domain.AccountStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
    List<User> findAllByOrganizationId(UUID organizationId);
    List<User> findAllByOrganizationIdAndAccountStatus(UUID organizationId, AccountStatus accountStatus);
    List<User> findAllByAccountStatus(AccountStatus accountStatus);
    boolean existsByIdAndOrganizationId(UUID id, UUID organizationId);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findWithRolesById(UUID id);

    // Add these to your existing UserRepository interface

    @EntityGraph(attributePaths = {"roles"})
    List<User> findAllWithRolesByOrganizationId(UUID organizationId);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findWithRolesByIdAndOrganizationId(UUID id, UUID organizationId);

    boolean existsByRoles_Id(UUID id);
}

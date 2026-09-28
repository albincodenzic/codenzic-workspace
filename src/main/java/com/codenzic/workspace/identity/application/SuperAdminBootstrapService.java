package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.Role;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.RoleRepository;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import com.codenzic.workspace.identity.presentation.dto.SuperAdminBootstrapRequest;
import com.codenzic.workspace.identity.presentation.dto.SuperAdminBootstrapResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Service
public class SuperAdminBootstrapService {
    private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
    private static final int MIN_SECRET_BYTES = 32;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final String configuredSecret;

    public SuperAdminBootstrapService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate,
            @Value("${security.bootstrap.super-admin-secret:}") String configuredSecret
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.configuredSecret = configuredSecret;
    }

    @Transactional
    public SuperAdminBootstrapResponse bootstrap(
            String suppliedSecret,
            SuperAdminBootstrapRequest request
    ) {
        if (configuredSecret.isBlank()) {
            throw new BusinessException(
                    "BOOTSTRAP_DISABLED", "Super-admin bootstrap is disabled", HttpStatus.NOT_FOUND);
        }

        byte[] expected = configuredSecret.getBytes(StandardCharsets.UTF_8);
        if (expected.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "SUPER_ADMIN_BOOTSTRAP_SECRET must be at least 32 bytes");
        }

        if (suppliedSecret == null
                || !MessageDigest.isEqual(expected, suppliedSecret.getBytes(StandardCharsets.UTF_8))) {
            throw new BusinessException(
                    "INVALID_BOOTSTRAP_SECRET", "Invalid bootstrap credential", HttpStatus.UNAUTHORIZED);
        }

        Boolean initialized = jdbcTemplate.queryForObject(
                "SELECT super_admin_initialized "
                        + "FROM platform_bootstrap_state WHERE id = 1 FOR UPDATE",
                Boolean.class);
        if (initialized == null) {
            throw new IllegalStateException("Super-admin bootstrap state is missing");
        }
        if (initialized) {
            throw new BusinessException(
                    "SUPER_ADMIN_ALREADY_BOOTSTRAPPED",
                    "Super-admin bootstrap has already been completed",
                    HttpStatus.CONFLICT);
        }

        Role superAdminRole = roleRepository
                .findByNameAndPlatformRoleTrueAndOrganizationIdIsNull(SUPER_ADMIN_ROLE)
                .orElseThrow(() -> new IllegalStateException(
                        "The SUPER_ADMIN role is missing; verify the database migrations"));

        if (userRepository.existsByRoles_Id(superAdminRole.getId())) {
            throw new BusinessException(
                    "SUPER_ADMIN_ALREADY_EXISTS",
                    "A super-admin user already exists",
                    HttpStatus.CONFLICT);
        }

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(
                    "EMAIL_ALREADY_REGISTERED",
                    "Email is already registered; use a new admin email",
                    HttpStatus.CONFLICT);
        }

        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName().trim(),
                request.lastName().trim(),
                null,
                AccountStatus.ACTIVE,
                true
        );
        user.assignRole(superAdminRole);
        User saved = userRepository.save(user);

        int updated = jdbcTemplate.update(
                "UPDATE platform_bootstrap_state "
                        + "SET super_admin_initialized = TRUE "
                        + "WHERE id = 1 AND super_admin_initialized = FALSE");
        if (updated != 1) {
            throw new IllegalStateException("Could not mark super-admin bootstrap complete");
        }

        return new SuperAdminBootstrapResponse(saved.getId(), saved.getEmail());
    }
}
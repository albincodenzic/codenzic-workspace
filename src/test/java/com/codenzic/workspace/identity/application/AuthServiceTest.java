package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.RefreshToken;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.PermissionRepository;
import com.codenzic.workspace.identity.infrastructure.RefreshTokenRepository;
import com.codenzic.workspace.identity.infrastructure.RoleRepository;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import com.codenzic.workspace.identity.presentation.dto.RefreshRequest;
import com.codenzic.workspace.identity.presentation.dto.RegisterRequest;
import com.codenzic.workspace.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private RoleRepository roleRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private AuditLogService auditLogService;

    @AfterEach
    void clearSecurityContext() {
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void refreshRotatesAndRevokesThePreviousOpaqueToken() {
        User user = new User("person@example.com", "hash", "Person", "Example", null, AccountStatus.ACTIVE, true);
        RefreshToken storedToken = new RefreshToken(user.getId(), "stored-hash", Instant.now().plus(Duration.ofDays(1)));
        when(refreshTokenRepository.findByTokenHashForUpdate(anyString())).thenReturn(Optional.of(storedToken));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AuthService service = new AuthService(
                userRepository,
                refreshTokenRepository,
                organizationRepository,
                passwordEncoder,
                authenticationManager,
                new JwtService("abcdefghijklmnopqrstuvwxyz123456", Duration.ofHours(1)),
                auditLogService,
                Duration.ofDays(30)
        );

        var response = service.refresh(new RefreshRequest("previous-token"));

        assertNotNull(response.refreshToken());
        assertNotEquals("previous-token", response.refreshToken());
        assertNotNull(storedToken.getRevokedAt());

        ArgumentCaptor<RefreshToken> savedToken = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(savedToken.capture());
        assertEquals(user.getId(), savedToken.getValue().getUserId());
        assertNotEquals(response.refreshToken(), savedToken.getValue().getTokenHash());

        assertThrows(BusinessException.class, () -> service.refresh(new RefreshRequest("previous-token")));
    }

    @Test
    void accessTokenIdentifiesOnlyTheUserAndCarriesNoTenantOrPermissionSnapshot() {
        User user = new User("person@example.com", "hash", "Person", "Example",
                UUID.randomUUID(), AccountStatus.ACTIVE, false);
        JwtService jwtService = new JwtService("abcdefghijklmnopqrstuvwxyz123456", Duration.ofHours(1));

        String token = jwtService.generateToken(user);

        assertEquals(user.getId(), jwtService.extractUserId(token));
        String payload = new String(java.util.Base64.getUrlDecoder().decode(token.split("\\.")[1]),
                java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertFalse(payload.contains("organizationId"));
        org.junit.jupiter.api.Assertions.assertFalse(payload.contains("roles"));
        org.junit.jupiter.api.Assertions.assertFalse(payload.contains("permissions"));
    }

    @Test
    void tenantRoleLookupUsesOnlyTheCurrentOrganization() {
        UUID organizationId = UUID.randomUUID();
        User user = new User("tenant@example.com", "hash", "Tenant", "User", organizationId, AccountStatus.ACTIVE, false);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()));
        RoleService roleService = new RoleService(roleRepository, permissionRepository, userRepository, auditLogService);
        UUID foreignRoleId = UUID.randomUUID();
        when(roleRepository.findByIdAndOrganizationId(foreignRoleId, organizationId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> roleService.get(foreignRoleId));

        assertEquals("ROLE_NOT_FOUND", exception.getCode());
        verify(roleRepository).findByIdAndOrganizationId(foreignRoleId, organizationId);
    }

    @Test
    void registrationCannotAssignAUserToAnotherOrganization() {
        UUID currentOrganizationId = UUID.randomUUID();
        User user = new User("tenant@example.com", "hash", "Tenant", "Admin",
                currentOrganizationId, AccountStatus.ACTIVE, false);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()));
        AuthService service = new AuthService(userRepository, refreshTokenRepository, organizationRepository,
                passwordEncoder, authenticationManager,
                new JwtService("abcdefghijklmnopqrstuvwxyz123456", Duration.ofHours(1)),
                auditLogService, Duration.ofDays(30));

        BusinessException error = assertThrows(BusinessException.class, () -> service.register(new RegisterRequest(
                "new@example.com", "a-long-enough-password", "New", "User", UUID.randomUUID())));

        assertEquals("ORGANIZATION_ACCESS_DENIED", error.getCode());
        org.mockito.Mockito.verifyNoInteractions(userRepository);
    }
}

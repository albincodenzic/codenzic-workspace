package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.RefreshToken;
import com.codenzic.workspace.identity.domain.Role;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.RefreshTokenRepository;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import com.codenzic.workspace.identity.presentation.dto.AuthResponse;
import com.codenzic.workspace.identity.presentation.dto.CurrentUserResponse;
import com.codenzic.workspace.identity.presentation.dto.LoginRequest;
import com.codenzic.workspace.identity.presentation.dto.RefreshRequest;
import com.codenzic.workspace.identity.presentation.dto.RegisterRequest;
import com.codenzic.workspace.organization.domain.OrganizationStatus;
import com.codenzic.workspace.organization.infrastructure.OrganizationRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService audit;
    private final Duration refreshExpiration;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuditLogService audit,
            @Value("${security.jwt.refresh-expiration:P30D}") Duration refreshExpiration
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.audit = audit;
        this.refreshExpiration = refreshExpiration;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        if (!organizationId.equals(request.organizationId())) {
            throw new BusinessException("ORGANIZATION_ACCESS_DENIED",
                    "You may only register users in your current organization", HttpStatus.FORBIDDEN);
        }
        if (!organizationRepository.existsByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)) {
            throw new BusinessException("ORGANIZATION_NOT_FOUND", "Active organization not found", HttpStatus.NOT_FOUND);
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("EMAIL_ALREADY_REGISTERED", "Email is already registered", HttpStatus.CONFLICT);
        }
        User user = new User(
                request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.firstName().trim(),
                request.lastName().trim(),
                organizationId,
                AccountStatus.ACTIVE,
                false
        );
        User saved = userRepository.save(user);
        audit.record(saved.getOrganizationId(), saved.getId(), "REGISTER", "AUTHENTICATION", saved.getId(), "{}");
        return toResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
        requireActiveOrganization(user);
        audit.record(user.getOrganizationId(), user.getId(), "LOGIN", "AUTHENTICATION", user.getId(), "{}");
        return toResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken token = refreshTokenRepository.findByTokenHashForUpdate(hashToken(request.refreshToken()))
                .orElseThrow(this::invalidRefreshToken);
        Instant now = Instant.now();
        if (!token.isUsableAt(now)) {
            throw invalidRefreshToken();
        }
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(this::invalidRefreshToken);
        if (!user.isEnabled() || !isOrganizationActive(user)) {
            token.revoke(now);
            throw invalidRefreshToken();
        }
        token.revoke(now);
        audit.record(user.getOrganizationId(), user.getId(), "REFRESH", "AUTHENTICATION", user.getId(), "{}");
        return toResponse(user);
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByTokenHashForUpdate(hashToken(request.refreshToken()))
                .ifPresent(token -> {
                    token.revoke(Instant.now());
                    userRepository.findById(token.getUserId()).ifPresent(user ->
                            audit.record(user.getOrganizationId(), user.getId(), "LOGOUT",
                                    "AUTHENTICATION", user.getId(), "{}"));
                });
    }

    public CurrentUserResponse me(User user) {
        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getOrganizationId(),
                user.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }

    private AuthResponse toResponse(User user) {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        String rawRefreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        refreshTokenRepository.save(new RefreshToken(
                user.getId(),
                hashToken(rawRefreshToken),
                Instant.now().plus(refreshExpiration)
        ));
        return new AuthResponse(
                jwtService.generateToken(user),
                rawRefreshToken,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getOrganizationId(),
                user.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }

    private String hashToken(String rawToken) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private BusinessException invalidRefreshToken() {
        return new BusinessException("INVALID_REFRESH_TOKEN", "Refresh token is invalid or expired", HttpStatus.UNAUTHORIZED);
    }

    private void requireActiveOrganization(User user) {
        if (!isOrganizationActive(user)) {
            throw new BusinessException("ORGANIZATION_INACTIVE", "The user's organization is inactive", HttpStatus.FORBIDDEN);
        }
    }

    private boolean isOrganizationActive(User user) {
        return user.getOrganizationId() == null
                || organizationRepository.existsByIdAndStatus(user.getOrganizationId(), OrganizationStatus.ACTIVE);
    }
}

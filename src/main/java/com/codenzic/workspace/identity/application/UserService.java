package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.common.exception.ResourceNotFoundException;
import com.codenzic.workspace.common.exception.ValidationException;
import com.codenzic.workspace.common.security.CurrentOrganizationContext;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import com.codenzic.workspace.identity.presentation.dto.UserResponse;
import com.codenzic.workspace.identity.presentation.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> list() {
        UUID orgId = getRequiredOrganizationContext();
        return userRepository.findAllWithRolesByOrganizationId(orgId)
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    public UserResponse get(UUID id) {
        UUID orgId = getRequiredOrganizationContext();
        User user = userRepository.findWithRolesByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        UUID orgId = getRequiredOrganizationContext();
        User user = userRepository.findWithRolesByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Ensure email uniqueness if changed
        if (!user.getEmail().equalsIgnoreCase(request.email()) 
                && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ValidationException("Email is already in use: " + request.email());
        }

        user.updateProfile(request.firstName(), request.lastName(), request.email());
        user.updateStatus(request.accountStatus());

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }

    @Transactional
    public void delete(UUID id) {
        UUID orgId = getRequiredOrganizationContext();
        User user = userRepository.findWithRolesByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        userRepository.delete(user);
    }

    private UUID getRequiredOrganizationContext() {
        UUID orgId = CurrentOrganizationContext.getOptionalSelectedOrganizationId()
                .orElseThrow(() -> new IllegalStateException("Organization context missing from current request thread."));
        if (orgId == null) {
            throw new IllegalStateException("Organization context missing from current request thread.");
        }
        return orgId;
    }
}
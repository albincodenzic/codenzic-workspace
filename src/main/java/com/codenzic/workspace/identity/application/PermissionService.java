package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.identity.infrastructure.PermissionRepository;
import com.codenzic.workspace.identity.presentation.dto.PermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<PermissionResponse> list() {
        return permissionRepository.findAllByOrderByCodeAsc().stream()
                .map(permission -> new PermissionResponse(permission.getId(), permission.getCode(), permission.getDescription()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponse get(String code) {
        return permissionRepository.findByCodeIgnoreCase(code)
                .map(permission -> new PermissionResponse(permission.getId(), permission.getCode(), permission.getDescription()))
                .orElseThrow(() -> new BusinessException("PERMISSION_NOT_FOUND", "Permission not found", HttpStatus.NOT_FOUND));
    }
}

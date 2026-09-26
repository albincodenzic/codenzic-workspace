package com.codenzic.workspace.common.api;

import java.time.Instant;

public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String message,
        T data,
        String path
) {
    public static <T> ApiResponse<T> success(T data, String path) {
        return new ApiResponse<>(Instant.now(), 200, "Success", data, path);
    }
}

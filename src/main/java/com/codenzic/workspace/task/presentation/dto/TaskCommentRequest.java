package com.codenzic.workspace.task.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Task Comment Request payload.")
public record TaskCommentRequest(@NotBlank @Size(max = 4000) String body) {
}

package com.codenzic.workspace.eod.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;

@Schema(description = "Eod Review Request payload.")
public record EodReviewRequest(@Size(max = 1000) String comment) {
}

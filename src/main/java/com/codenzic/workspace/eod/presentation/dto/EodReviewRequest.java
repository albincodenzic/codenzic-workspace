package com.codenzic.workspace.eod.presentation.dto;

import jakarta.validation.constraints.Size;

public record EodReviewRequest(@Size(max = 1000) String comment) {
}

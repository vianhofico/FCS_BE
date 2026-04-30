package com.fcs.be.modules.iam.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserTokenPreviewRequest(@NotNull UUID userId) {
}
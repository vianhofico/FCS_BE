package com.fcs.be.modules.iam.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
    @NotBlank String code,
    @NotBlank String name,
    String description,
    String module
) {}

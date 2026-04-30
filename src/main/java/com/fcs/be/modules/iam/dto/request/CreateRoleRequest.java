package com.fcs.be.modules.iam.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
    @NotBlank String name,
    String description
) {}

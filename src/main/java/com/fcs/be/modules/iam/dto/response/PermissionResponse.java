package com.fcs.be.modules.iam.dto.response;

import java.util.UUID;

public record PermissionResponse(
    UUID id,
    String code,
    String name,
    String description,
    String module
) {}

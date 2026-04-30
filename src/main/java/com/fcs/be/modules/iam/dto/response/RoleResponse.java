package com.fcs.be.modules.iam.dto.response;

import java.util.UUID;

public record RoleResponse(
    UUID id,
    String name,
    String description
) {}

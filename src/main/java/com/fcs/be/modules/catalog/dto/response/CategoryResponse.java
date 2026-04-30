package com.fcs.be.modules.catalog.dto.response;

import java.util.UUID;

public record CategoryResponse(
    UUID id,
    UUID parentId,
    String name,
    String slug,
    boolean active
) {
}

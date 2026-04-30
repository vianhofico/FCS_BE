package com.fcs.be.modules.catalog.dto.response;

import java.util.UUID;

public record BrandResponse(
    UUID id,
    String name,
    String logoUrl,
    String description,
    boolean active
) {
}

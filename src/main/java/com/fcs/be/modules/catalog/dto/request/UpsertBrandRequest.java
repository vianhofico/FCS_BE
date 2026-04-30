package com.fcs.be.modules.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpsertBrandRequest(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 500) String logoUrl,
    @Size(max = 1000) String description,
    boolean active
) {
}

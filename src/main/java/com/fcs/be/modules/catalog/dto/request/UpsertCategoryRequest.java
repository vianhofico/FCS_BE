package com.fcs.be.modules.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpsertCategoryRequest(
    UUID parentId,
    @NotBlank @Size(max = 150) String name,
    @NotBlank @Size(max = 180) String slug,
    boolean active
) {
}

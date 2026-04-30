package com.fcs.be.modules.product.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record AssignProductCategoriesRequest(
    @NotNull List<UUID> categoryIds,
    UUID primaryCategoryId
) {}

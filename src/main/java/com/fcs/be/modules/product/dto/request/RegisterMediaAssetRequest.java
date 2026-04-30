package com.fcs.be.modules.product.dto.request;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.common.enums.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegisterMediaAssetRequest(
    @NotNull MediaOwnerType ownerType,
    @NotNull UUID ownerId,
    @NotNull MediaType mediaType,
    @NotBlank String url,
    String thumbnailUrl,
    String mimeType,
    Long sizeBytes,
    Integer displayOrder,
    boolean isPrimary
) {}

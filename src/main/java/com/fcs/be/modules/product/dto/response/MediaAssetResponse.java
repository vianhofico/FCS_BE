package com.fcs.be.modules.product.dto.response;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.common.enums.MediaType;
import java.util.UUID;

public record MediaAssetResponse(
    UUID id,
    MediaOwnerType ownerType,
    UUID ownerId,
    MediaType mediaType,
    String url,
    String thumbnailUrl,
    String mimeType,
    Long sizeBytes,
    Integer displayOrder,
    boolean isPrimary
) {}

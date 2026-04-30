package com.fcs.be.modules.catalog.dto.response;

import java.util.UUID;

public record SystemSettingResponse(
    UUID id,
    String key,
    String value,
    String description
) {
}

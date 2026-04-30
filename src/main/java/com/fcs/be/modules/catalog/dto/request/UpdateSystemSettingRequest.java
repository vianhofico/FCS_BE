package com.fcs.be.modules.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSystemSettingRequest(
    @NotBlank @Size(max = 2000) String value,
    @Size(max = 1000) String description
) {
}

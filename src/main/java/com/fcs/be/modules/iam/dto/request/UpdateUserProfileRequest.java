package com.fcs.be.modules.iam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 80, message = "Username must be between 3 and 80 characters")
    String username,

    @Size(max = 30, message = "Phone must be at most 30 characters")
    String phone
) {}

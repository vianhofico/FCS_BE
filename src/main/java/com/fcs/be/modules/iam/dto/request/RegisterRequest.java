package com.fcs.be.modules.iam.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 80) String username,
    @NotBlank @Email @Size(max = 180) String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    @Size(max = 30) String phone
) {}

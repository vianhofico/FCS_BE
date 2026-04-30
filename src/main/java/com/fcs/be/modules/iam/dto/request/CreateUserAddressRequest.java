package com.fcs.be.modules.iam.dto.request;

import com.fcs.be.common.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserAddressRequest(
    @NotBlank String fullName,
    @NotBlank String phone,
    @NotBlank String street,
    @NotBlank String ward,
    @NotBlank String district,
    @NotBlank String city,
    boolean isDefault,
    @NotNull AddressType type
) {}

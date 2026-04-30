package com.fcs.be.modules.iam.dto.response;

import com.fcs.be.common.enums.AddressType;
import java.util.UUID;

public record UserAddressResponse(
    UUID id,
    UUID userId,
    String fullName,
    String phone,
    String street,
    String ward,
    String district,
    String city,
    boolean isDefault,
    AddressType type
) {}

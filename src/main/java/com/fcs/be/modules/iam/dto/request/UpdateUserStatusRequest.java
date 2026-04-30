package com.fcs.be.modules.iam.dto.request;

import com.fcs.be.common.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
    @NotNull UserStatus status
) {}

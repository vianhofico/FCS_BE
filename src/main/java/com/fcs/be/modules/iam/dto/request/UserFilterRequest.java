package com.fcs.be.modules.iam.dto.request;

import com.fcs.be.common.enums.UserStatus;

public record UserFilterRequest(
    String keyword,
    String role,
    UserStatus status
) {}

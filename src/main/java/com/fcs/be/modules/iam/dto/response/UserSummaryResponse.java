package com.fcs.be.modules.iam.dto.response;

import com.fcs.be.common.enums.UserStatus;
import java.util.UUID;

public record UserSummaryResponse(
    UUID id,
    String username,
    String email,
    String phone,
    UserStatus status
) {
}

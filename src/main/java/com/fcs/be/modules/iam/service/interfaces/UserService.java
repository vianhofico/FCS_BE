package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserSummaryResponse> getUsers();

    UserSummaryResponse getUser(UUID id);

    UserSummaryResponse updateStatus(UUID id, UserStatus status);

    void assignRoles(UUID userId, List<UUID> roleIds);

    void removeRole(UUID userId, UUID roleId);
}
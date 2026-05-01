package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.dto.request.ChangePasswordRequest;
import com.fcs.be.modules.iam.dto.request.UpdateUserProfileRequest;
import com.fcs.be.modules.iam.dto.request.UserFilterRequest;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageResponse<UserSummaryResponse> getUsers(UserFilterRequest filter, Pageable pageable);

    UserSummaryResponse getUser(UUID id);

    UserSummaryResponse updateProfile(UUID id, UpdateUserProfileRequest request);

    void changePassword(UUID id, ChangePasswordRequest request);

    UserSummaryResponse updateStatus(UUID id, UserStatus status);

    void assignRoles(UUID userId, List<UUID> roleIds);

    void removeRole(UUID userId, UUID roleId);
}


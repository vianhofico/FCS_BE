package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import java.util.UUID;

public interface UserService {

    UserSummaryResponse getUser(UUID id);
}
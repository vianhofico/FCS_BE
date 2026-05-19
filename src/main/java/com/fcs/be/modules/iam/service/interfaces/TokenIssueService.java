package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.response.AuthResponse;
import com.fcs.be.modules.iam.entity.AuthIdentity;
import com.fcs.be.modules.iam.entity.User;

public interface TokenIssueService {

    AuthResponse issueTokenPair(User user, AuthIdentity identity);
}

package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.response.TokenPreviewResponse;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import java.util.UUID;

public interface IamService {

    UserSummaryResponse getUser(UUID id);

    TokenPreviewResponse previewTokens(UUID userId);
}

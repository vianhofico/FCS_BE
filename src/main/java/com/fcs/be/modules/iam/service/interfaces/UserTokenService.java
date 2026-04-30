package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.response.UserTokenPreviewResponse;
import java.util.UUID;

public interface UserTokenService {

    UserTokenPreviewResponse previewTokens(UUID userId);
}
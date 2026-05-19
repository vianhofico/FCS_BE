package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.response.AuthResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2AuthService {

    AuthResponse authenticateGoogleUser(OAuth2User principal);
}

package com.fcs.be.modules.iam.service.interfaces;

import com.fcs.be.modules.iam.dto.request.LoginRequest;
import com.fcs.be.modules.iam.dto.request.RefreshTokenRequest;
import com.fcs.be.modules.iam.dto.request.RegisterRequest;
import com.fcs.be.modules.iam.dto.response.AuthResponse;
import java.util.UUID;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(UUID userId);
}

package com.fcs.be.config;

import com.fcs.be.modules.iam.dto.response.AuthResponse;
import com.fcs.be.modules.iam.service.interfaces.OAuth2AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthService oauth2AuthService;
    private final String successRedirectUri;
    private final String failureRedirectUri;

    public OAuth2AuthenticationSuccessHandler(
        OAuth2AuthService oauth2AuthService,
        @Value("${app.oauth2.success-redirect-uri:http://localhost:5173/auth/oauth2/callback}") String successRedirectUri,
        @Value("${app.oauth2.failure-redirect-uri:http://localhost:5173/auth/login}") String failureRedirectUri
    ) {
        this.oauth2AuthService = oauth2AuthService;
        this.successRedirectUri = successRedirectUri;
        this.failureRedirectUri = failureRedirectUri;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        try {
            OAuth2User principal = (OAuth2User) authentication.getPrincipal();
            AuthResponse authResponse = oauth2AuthService.authenticateGoogleUser(principal);
            String redirectUri = UriComponentsBuilder.fromUriString(successRedirectUri)
                .queryParam("accessToken", authResponse.accessToken())
                .queryParam("refreshToken", authResponse.refreshToken())
                .queryParam("userId", authResponse.userId())
                .queryParam("username", authResponse.username())
                .queryParam("email", authResponse.email())
                .queryParam("fullName", authResponse.fullName())
                .queryParam("roles", String.join(",", authResponse.roles()))
                .build()
                .encode()
                .toUriString();
            response.sendRedirect(redirectUri);
        } catch (RuntimeException ex) {
            String redirectUri = failureRedirectUri + "?oauthError=" + URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(redirectUri);
        }
    }
}

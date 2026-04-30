package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.iam.dto.request.UserTokenPreviewRequest;
import com.fcs.be.modules.iam.dto.response.UserTokenPreviewResponse;
import com.fcs.be.modules.iam.service.interfaces.UserTokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/auth/token")
public class UserTokenPreviewController {

    private final UserTokenService userTokenService;

    public UserTokenPreviewController(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    @PostMapping("/preview")
    public ResponseEntity<ApiResponse<UserTokenPreviewResponse>> previewTokens(
        @Valid @RequestBody UserTokenPreviewRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Generated token preview", userTokenService.previewTokens(request.userId())));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }
}
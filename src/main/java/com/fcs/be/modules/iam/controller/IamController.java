package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.iam.dto.request.TokenPreviewRequest;
import com.fcs.be.modules.iam.dto.response.TokenPreviewResponse;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.service.interfaces.IamService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam")
public class IamController {

    private final IamService iamService;

    public IamController(IamService iamService) {
        this.iamService = iamService;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getUser(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched user", iamService.getUser(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/auth/token/preview")
    public ResponseEntity<ApiResponse<TokenPreviewResponse>> previewTokens(
        @Valid @RequestBody TokenPreviewRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Generated token preview", iamService.previewTokens(request.userId())));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }
}

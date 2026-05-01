package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.dto.request.UpdateUserStatusRequest;
import com.fcs.be.modules.iam.dto.request.UserFilterRequest;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.service.interfaces.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getUsers(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) UserStatus status,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        UserFilterRequest filter = new UserFilterRequest(keyword, role, status);
        return ResponseEntity.ok(ApiResponse.ok("Fetched users", userService.getUsers(filter, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched user", userService.getUser(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> updateProfile(
        @PathVariable UUID id,
        @Valid @RequestBody com.fcs.be.modules.iam.dto.request.UpdateUserProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("User profile updated", userService.updateProfile(id, request)));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
        @PathVariable UUID id,
        @Valid @RequestBody com.fcs.be.modules.iam.dto.request.ChangePasswordRequest request
    ) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("User status updated", userService.updateStatus(id, request.status())));
    }
}


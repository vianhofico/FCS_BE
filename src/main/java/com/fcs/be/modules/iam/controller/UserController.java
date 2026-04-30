package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.iam.dto.request.UpdateUserStatusRequest;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.service.interfaces.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched users", userService.getUsers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched user", userService.getUser(id)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> updateStatus(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("User status updated", userService.updateStatus(id, request.status())));
    }
}

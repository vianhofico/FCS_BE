package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.iam.dto.response.UserSummaryResponse;
import com.fcs.be.modules.iam.service.interfaces.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getUser(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Fetched user", userService.getUser(id)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}
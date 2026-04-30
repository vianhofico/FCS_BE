package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.iam.dto.request.AssignRolesRequest;
import com.fcs.be.modules.iam.service.interfaces.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/users")
public class UserRoleController {

    private final UserService userService;

    public UserRoleController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<Void>> assignRoles(@PathVariable UUID userId, @Valid @RequestBody AssignRolesRequest request) {
        userService.assignRoles(userId, request.roleIds());
        return ResponseEntity.ok(ApiResponse.ok("Roles assigned to user"));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userService.removeRole(userId, roleId);
        return ResponseEntity.ok(ApiResponse.ok("Role removed from user"));
    }
}

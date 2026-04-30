package com.fcs.be.modules.iam.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.iam.dto.request.CreatePermissionRequest;
import com.fcs.be.modules.iam.dto.response.PermissionResponse;
import com.fcs.be.modules.iam.service.interfaces.PermissionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/iam/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissions() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched permissions", permissionService.getPermissions()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Permission created", permissionService.createPermission(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(@PathVariable UUID id, @Valid @RequestBody CreatePermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Permission updated", permissionService.updatePermission(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.ok("Permission deleted"));
    }
}

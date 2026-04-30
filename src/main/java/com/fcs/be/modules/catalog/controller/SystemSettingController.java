package com.fcs.be.modules.catalog.controller;

import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.catalog.dto.request.UpdateSystemSettingRequest;
import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import com.fcs.be.modules.catalog.service.interfaces.SystemSettingService;
import jakarta.persistence.EntityNotFoundException;
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
@RequestMapping("/api/v1/catalog/settings")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    public SystemSettingController(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemSettingResponse>>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok("Fetched settings", systemSettingService.getSettings()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SystemSettingResponse>> updateSetting(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateSystemSettingRequest request
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.ok("Setting updated", systemSettingService.updateSetting(id, request)));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(404).body(ApiResponse.error(ex.getMessage()));
        }
    }
}
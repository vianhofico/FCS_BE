package com.fcs.be.modules.product.controller;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.common.response.ApiResponse;
import com.fcs.be.modules.product.dto.request.RegisterMediaAssetRequest;
import com.fcs.be.modules.product.dto.response.MediaAssetResponse;
import com.fcs.be.modules.product.service.interfaces.MediaAssetService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/media")
public class MediaAssetController {

    private final MediaAssetService mediaAssetService;

    public MediaAssetController(MediaAssetService mediaAssetService) {
        this.mediaAssetService = mediaAssetService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MediaAssetResponse>> registerAsset(
        @Valid @RequestBody RegisterMediaAssetRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Media asset registered", mediaAssetService.registerAsset(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaAssetResponse>>> getAssets(
        @RequestParam MediaOwnerType ownerType,
        @RequestParam UUID ownerId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched media assets", mediaAssetService.getAssetsByOwner(ownerType, ownerId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable UUID id) {
        mediaAssetService.deleteAsset(id);
        return ResponseEntity.ok(ApiResponse.ok("Media asset deleted"));
    }
}

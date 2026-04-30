package com.fcs.be.modules.product.service.impl;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.modules.product.dto.request.RegisterMediaAssetRequest;
import com.fcs.be.modules.product.dto.response.MediaAssetResponse;
import com.fcs.be.modules.product.entity.MediaAsset;
import com.fcs.be.modules.product.mapper.MediaAssetMapper;
import com.fcs.be.modules.product.repository.MediaAssetRepository;
import com.fcs.be.modules.product.service.interfaces.MediaAssetService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaAssetServiceImpl implements MediaAssetService {

    private final MediaAssetRepository mediaAssetRepository;
    private final MediaAssetMapper mediaAssetMapper;

    public MediaAssetServiceImpl(MediaAssetRepository mediaAssetRepository, MediaAssetMapper mediaAssetMapper) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.mediaAssetMapper = mediaAssetMapper;
    }

    @Override
    @Transactional
    public MediaAssetResponse registerAsset(RegisterMediaAssetRequest request) {
        MediaAsset asset = new MediaAsset();
        asset.setOwnerType(request.ownerType());
        asset.setOwnerId(request.ownerId());
        asset.setMediaType(request.mediaType());
        asset.setUrl(request.url());
        asset.setThumbnailUrl(request.thumbnailUrl());
        asset.setMimeType(request.mimeType());
        asset.setSizeBytes(request.sizeBytes());
        asset.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 0);
        asset.setPrimary(request.isPrimary());
        return mediaAssetMapper.toResponse(mediaAssetRepository.save(asset));
    }

    @Override
    public List<MediaAssetResponse> getAssetsByOwner(MediaOwnerType ownerType, UUID ownerId) {
        return mediaAssetRepository
            .findByOwnerTypeAndOwnerIdAndIsDeletedFalseOrderByDisplayOrderAsc(ownerType, ownerId)
            .stream()
            .map(mediaAssetMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public void deleteAsset(UUID id) {
        MediaAsset asset = mediaAssetRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Media asset not found"));
        asset.setDeleted(true);
        mediaAssetRepository.save(asset);
    }
}

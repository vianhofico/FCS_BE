package com.fcs.be.modules.product.service.impl;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.config.AppConfigHelper;
import com.fcs.be.modules.product.dto.request.RegisterMediaAssetRequest;
import com.fcs.be.modules.product.dto.response.MediaAssetResponse;
import com.fcs.be.modules.product.dto.response.UploadMediaResponse;
import com.fcs.be.modules.product.entity.MediaAsset;
import com.fcs.be.modules.product.mapper.MediaAssetMapper;
import com.fcs.be.modules.product.repository.MediaAssetRepository;
import com.fcs.be.modules.product.service.interfaces.MediaAssetService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.persistence.EntityNotFoundException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaAssetServiceImpl implements MediaAssetService {

    private final MediaAssetRepository mediaAssetRepository;
    private final MediaAssetMapper mediaAssetMapper;
    private final MinioClient minioClient;
    private final AppConfigHelper appConfigHelper;

    public MediaAssetServiceImpl(
        MediaAssetRepository mediaAssetRepository,
        MediaAssetMapper mediaAssetMapper,
        MinioClient minioClient,
        AppConfigHelper appConfigHelper
    ) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.mediaAssetMapper = mediaAssetMapper;
        this.minioClient = minioClient;
        this.appConfigHelper = appConfigHelper;
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
    public List<UploadMediaResponse> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("At least one image file is required");
        }

        return files.stream().map(this::uploadImage).toList();
    }

    @Override
    public List<MediaAssetResponse> getAssetsByOwner(MediaOwnerType ownerType, UUID ownerId) {
        return mediaAssetRepository
            .findByOwnerTypeAndOwnerIdAndIsDeletedFalseOrderByDisplayOrderAsc(ownerType, ownerId)
            .stream()
            .map(mediaAssetMapper::toResponse)
            .toList();
    }

    private UploadMediaResponse uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Image file must not be empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        String fileName = sanitizeFileName(file.getOriginalFilename());
        String objectKey = "media/images/" + UUID.randomUUID() + "-" + fileName;

        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(appConfigHelper.minioBucket())
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1L)
                    .contentType(contentType)
                    .build()
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to upload image", ex);
        }

        return new UploadMediaResponse(
            fileName,
            objectKey,
            publicUrl(objectKey),
            contentType,
            file.getSize()
        );
    }

    private String sanitizeFileName(String originalFileName) {
        String fileName = originalFileName == null || originalFileName.isBlank() ? "image" : originalFileName;
        String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replaceAll("[^a-zA-Z0-9._-]", "-")
            .replaceAll("-+", "-");
        return normalized.toLowerCase(Locale.ROOT);
    }

    private String publicUrl(String objectKey) {
        return appConfigHelper.minioEndpoint() + "/" + appConfigHelper.minioBucket() + "/" + objectKey;
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

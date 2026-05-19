package com.fcs.be.modules.product.service.interfaces;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.modules.product.dto.request.RegisterMediaAssetRequest;
import com.fcs.be.modules.product.dto.response.MediaAssetResponse;
import com.fcs.be.modules.product.dto.response.UploadMediaResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MediaAssetService {

    MediaAssetResponse registerAsset(RegisterMediaAssetRequest request);

    List<UploadMediaResponse> uploadImages(List<MultipartFile> files);

    List<MediaAssetResponse> getAssetsByOwner(MediaOwnerType ownerType, UUID ownerId);

    void deleteAsset(UUID id);
}

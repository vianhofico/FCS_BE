package com.fcs.be.modules.product.service.interfaces;

import com.fcs.be.common.enums.MediaOwnerType;
import com.fcs.be.modules.product.dto.request.RegisterMediaAssetRequest;
import com.fcs.be.modules.product.dto.response.MediaAssetResponse;
import java.util.List;
import java.util.UUID;

public interface MediaAssetService {

    MediaAssetResponse registerAsset(RegisterMediaAssetRequest request);

    List<MediaAssetResponse> getAssetsByOwner(MediaOwnerType ownerType, UUID ownerId);

    void deleteAsset(UUID id);
}

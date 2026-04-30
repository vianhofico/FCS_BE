package com.fcs.be.modules.product.mapper;

import com.fcs.be.modules.product.dto.response.MediaAssetResponse;
import com.fcs.be.modules.product.entity.MediaAsset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MediaAssetMapper {

    @Mapping(target = "isPrimary", source = "primary")
    MediaAssetResponse toResponse(MediaAsset mediaAsset);
}
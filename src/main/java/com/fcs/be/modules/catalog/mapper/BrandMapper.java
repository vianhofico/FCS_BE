package com.fcs.be.modules.catalog.mapper;

import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import com.fcs.be.modules.catalog.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {

    public BrandResponse toBrandResponse(Brand brand) {
        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            brand.getLogoUrl(),
            brand.getDescription(),
            brand.isActive()
        );
    }
}
package com.fcs.be.modules.catalog.mapper;

import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import com.fcs.be.modules.catalog.entity.Brand;
import com.fcs.be.modules.catalog.entity.Category;
import com.fcs.be.modules.catalog.entity.SystemSetting;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getParent() == null ? null : category.getParent().getId(),
            category.getName(),
            category.getSlug(),
            category.isActive()
        );
    }

    public BrandResponse toBrandResponse(Brand brand) {
        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            brand.getLogoUrl(),
            brand.getDescription(),
            brand.isActive()
        );
    }

    public SystemSettingResponse toSystemSettingResponse(SystemSetting systemSetting) {
        return new SystemSettingResponse(
            systemSetting.getId(),
            systemSetting.getKey(),
            systemSetting.getValue(),
            systemSetting.getDescription()
        );
    }
}

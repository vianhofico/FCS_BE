package com.fcs.be.modules.catalog.service.interfaces;

import com.fcs.be.modules.catalog.dto.request.UpdateSystemSettingRequest;
import com.fcs.be.modules.catalog.dto.request.UpsertBrandRequest;
import com.fcs.be.modules.catalog.dto.request.UpsertCategoryRequest;
import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import java.util.List;
import java.util.UUID;

public interface CatalogService {

    List<CategoryResponse> getCategories();

    CategoryResponse getCategory(UUID id);

    CategoryResponse createCategory(UpsertCategoryRequest request);

    CategoryResponse updateCategory(UUID id, UpsertCategoryRequest request);

    void deleteCategory(UUID id);

    List<BrandResponse> getBrands();

    BrandResponse getBrand(UUID id);

    BrandResponse createBrand(UpsertBrandRequest request);

    BrandResponse updateBrand(UUID id, UpsertBrandRequest request);

    void deleteBrand(UUID id);

    List<SystemSettingResponse> getSettings();

    SystemSettingResponse updateSetting(UUID id, UpdateSystemSettingRequest request);
}

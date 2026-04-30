package com.fcs.be.modules.catalog.service.interfaces;

import com.fcs.be.modules.catalog.dto.request.UpsertCategoryRequest;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryResponse> getCategories();

    CategoryResponse getCategory(UUID id);

    CategoryResponse createCategory(UpsertCategoryRequest request);

    CategoryResponse updateCategory(UUID id, UpsertCategoryRequest request);

    void deleteCategory(UUID id);
}
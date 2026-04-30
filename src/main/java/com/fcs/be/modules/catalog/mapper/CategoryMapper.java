package com.fcs.be.modules.catalog.mapper;

import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getParent() == null ? null : category.getParent().getId(),
            category.getName(),
            category.getSlug(),
            category.isActive()
        );
    }
}
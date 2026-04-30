package com.fcs.be.modules.catalog.service.impl;

import com.fcs.be.modules.catalog.dto.request.UpsertCategoryRequest;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.entity.Category;
import com.fcs.be.modules.catalog.mapper.CategoryMapper;
import com.fcs.be.modules.catalog.repository.CategoryRepository;
import com.fcs.be.modules.catalog.service.interfaces.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(categoryMapper::toCategoryResponse)
            .toList();
    }

    @Override
    public CategoryResponse getCategory(UUID id) {
        return categoryMapper.toCategoryResponse(getCategoryEntity(id));
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(UpsertCategoryRequest request) {
        Category category = new Category();
        applyCategory(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, UpsertCategoryRequest request) {
        Category category = getCategoryEntity(id);
        applyCategory(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getCategoryEntity(id);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private Category getCategoryEntity(UUID id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    private void applyCategory(Category category, UpsertCategoryRequest request) {
        if (request.parentId() == null) {
            category.setParent(null);
        } else {
            category.setParent(getCategoryEntity(request.parentId()));
        }
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setActive(request.active());
    }
}
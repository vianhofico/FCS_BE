package com.fcs.be.modules.catalog.service.impl;

import com.fcs.be.modules.catalog.dto.request.UpdateSystemSettingRequest;
import com.fcs.be.modules.catalog.dto.request.UpsertBrandRequest;
import com.fcs.be.modules.catalog.dto.request.UpsertCategoryRequest;
import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.dto.response.SystemSettingResponse;
import com.fcs.be.modules.catalog.entity.Brand;
import com.fcs.be.modules.catalog.entity.Category;
import com.fcs.be.modules.catalog.entity.SystemSetting;
import com.fcs.be.modules.catalog.mapper.CatalogMapper;
import com.fcs.be.modules.catalog.repository.BrandRepository;
import com.fcs.be.modules.catalog.repository.CategoryRepository;
import com.fcs.be.modules.catalog.repository.SystemSettingRepository;
import com.fcs.be.modules.catalog.service.interfaces.CatalogService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final CatalogMapper catalogMapper;

    public CatalogServiceImpl(
        CategoryRepository categoryRepository,
        BrandRepository brandRepository,
        SystemSettingRepository systemSettingRepository,
        CatalogMapper catalogMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.catalogMapper = catalogMapper;
    }

    @Override
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(catalogMapper::toCategoryResponse)
            .toList();
    }

    @Override
    public CategoryResponse getCategory(UUID id) {
        return catalogMapper.toCategoryResponse(getCategoryEntity(id));
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(UpsertCategoryRequest request) {
        Category category = new Category();
        applyCategory(category, request);
        return catalogMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, UpsertCategoryRequest request) {
        Category category = getCategoryEntity(id);
        applyCategory(category, request);
        return catalogMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = getCategoryEntity(id);
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    @Override
    public List<BrandResponse> getBrands() {
        return brandRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(catalogMapper::toBrandResponse)
            .toList();
    }

    @Override
    public BrandResponse getBrand(UUID id) {
        return catalogMapper.toBrandResponse(getBrandEntity(id));
    }

    @Override
    @Transactional
    public BrandResponse createBrand(UpsertBrandRequest request) {
        Brand brand = new Brand();
        applyBrand(brand, request);
        return catalogMapper.toBrandResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(UUID id, UpsertBrandRequest request) {
        Brand brand = getBrandEntity(id);
        applyBrand(brand, request);
        return catalogMapper.toBrandResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void deleteBrand(UUID id) {
        Brand brand = getBrandEntity(id);
        brand.setDeleted(true);
        brandRepository.save(brand);
    }

    @Override
    public List<SystemSettingResponse> getSettings() {
        return systemSettingRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(catalogMapper::toSystemSettingResponse)
            .toList();
    }

    @Override
    @Transactional
    public SystemSettingResponse updateSetting(UUID id, UpdateSystemSettingRequest request) {
        SystemSetting setting = systemSettingRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("System setting not found"));
        setting.setValue(request.value());
        setting.setDescription(request.description());
        return catalogMapper.toSystemSettingResponse(systemSettingRepository.save(setting));
    }

    private Category getCategoryEntity(UUID id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    private Brand getBrandEntity(UUID id) {
        return brandRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
    }

    private void applyCategory(Category category, UpsertCategoryRequest request) {
        if (request.parentId() != null) {
            category.setParent(getCategoryEntity(request.parentId()));
        } else {
            category.setParent(null);
        }
        category.setName(request.name());
        category.setSlug(request.slug());
        category.setActive(request.active());
    }

    private void applyBrand(Brand brand, UpsertBrandRequest request) {
        brand.setName(request.name());
        brand.setLogoUrl(request.logoUrl());
        brand.setDescription(request.description());
        brand.setActive(request.active());
    }
}

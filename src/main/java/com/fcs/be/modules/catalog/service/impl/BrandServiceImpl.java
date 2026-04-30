package com.fcs.be.modules.catalog.service.impl;

import com.fcs.be.modules.catalog.dto.request.UpsertBrandRequest;
import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import com.fcs.be.modules.catalog.entity.Brand;
import com.fcs.be.modules.catalog.mapper.BrandMapper;
import com.fcs.be.modules.catalog.repository.BrandRepository;
import com.fcs.be.modules.catalog.service.interfaces.BrandService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<BrandResponse> getBrands() {
        return brandRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            .stream()
            .map(brandMapper::toBrandResponse)
            .toList();
    }

    @Override
    public BrandResponse getBrand(UUID id) {
        return brandMapper.toBrandResponse(getBrandEntity(id));
    }

    @Override
    @Transactional
    public BrandResponse createBrand(UpsertBrandRequest request) {
        Brand brand = new Brand();
        applyBrand(brand, request);
        return brandMapper.toBrandResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(UUID id, UpsertBrandRequest request) {
        Brand brand = getBrandEntity(id);
        applyBrand(brand, request);
        return brandMapper.toBrandResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void deleteBrand(UUID id) {
        Brand brand = getBrandEntity(id);
        brand.setDeleted(true);
        brandRepository.save(brand);
    }

    private Brand getBrandEntity(UUID id) {
        return brandRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
    }

    private void applyBrand(Brand brand, UpsertBrandRequest request) {
        brand.setName(request.name());
        brand.setLogoUrl(request.logoUrl());
        brand.setDescription(request.description());
        brand.setActive(request.active());
    }
}
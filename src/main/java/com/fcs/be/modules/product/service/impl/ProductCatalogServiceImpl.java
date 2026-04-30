package com.fcs.be.modules.product.service.impl;

import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import com.fcs.be.modules.catalog.entity.Category;
import com.fcs.be.modules.catalog.mapper.CategoryMapper;
import com.fcs.be.modules.catalog.repository.CategoryRepository;
import com.fcs.be.modules.product.dto.request.AssignProductCategoriesRequest;
import com.fcs.be.modules.product.dto.request.CreateWarehouseLogRequest;
import com.fcs.be.modules.product.dto.response.WarehouseLogResponse;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.entity.ProductCategory;
import com.fcs.be.modules.product.entity.WarehouseLog;
import com.fcs.be.modules.product.mapper.WarehouseLogMapper;
import com.fcs.be.modules.product.repository.ProductCategoryRepository;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.product.repository.WarehouseLogRepository;
import com.fcs.be.modules.product.service.interfaces.ProductCatalogService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCatalogServiceImpl implements ProductCatalogService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final WarehouseLogRepository warehouseLogRepository;
    private final CategoryMapper categoryMapper;
    private final WarehouseLogMapper warehouseLogMapper;

    public ProductCatalogServiceImpl(
        ProductRepository productRepository,
        ProductCategoryRepository productCategoryRepository,
        CategoryRepository categoryRepository,
        WarehouseLogRepository warehouseLogRepository,
        CategoryMapper categoryMapper,
        WarehouseLogMapper warehouseLogMapper
    ) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.warehouseLogRepository = warehouseLogRepository;
        this.categoryMapper = categoryMapper;
        this.warehouseLogMapper = warehouseLogMapper;
    }

    @Override
    public List<CategoryResponse> getProductCategories(UUID productId) {
        return productCategoryRepository.findByProductIdAndIsDeletedFalse(productId)
            .stream()
            .map(pc -> categoryMapper.toCategoryResponse(pc.getCategory()))
            .toList();
    }

    @Override
    @Transactional
    public void assignCategories(UUID productId, AssignProductCategoriesRequest request) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        productCategoryRepository.softDeleteAllByProductId(productId);

        for (UUID categoryId : request.categoryIds()) {
            Category category = categoryRepository.findByIdAndIsDeletedFalse(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
            ProductCategory pc = new ProductCategory();
            pc.setProduct(product);
            pc.setCategory(category);
            pc.setPrimary(categoryId.equals(request.primaryCategoryId()));
            productCategoryRepository.save(pc);
        }
    }

    @Override
    @Transactional
    public WarehouseLogResponse createWarehouseLog(CreateWarehouseLogRequest request) {
        Product product = productRepository.findByIdAndIsDeletedFalse(request.productId())
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        WarehouseLog log = new WarehouseLog();
        log.setProduct(product);
        log.setLocation(request.location());
        log.setActionType(request.actionType());
        log.setNote(request.note());

        return warehouseLogMapper.toResponse(warehouseLogRepository.save(log));
    }

    @Override
    public List<WarehouseLogResponse> getWarehouseLogs(UUID productId) {
        return warehouseLogRepository.findByProductIdOrderByCreatedAtDesc(productId)
            .stream()
            .map(warehouseLogMapper::toResponse)
            .toList();
    }
}

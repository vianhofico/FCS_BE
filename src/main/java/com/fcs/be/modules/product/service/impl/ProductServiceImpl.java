package com.fcs.be.modules.product.service.impl;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.catalog.entity.Brand;
import com.fcs.be.modules.catalog.repository.BrandRepository;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.product.dto.request.CreateProductRequest;
import com.fcs.be.modules.product.dto.request.UpdateProductRequest;
import com.fcs.be.modules.product.dto.response.ProductResponse;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.entity.ProductStatusHistory;
import com.fcs.be.modules.product.mapper.ProductMapper;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.product.repository.ProductStatusHistoryRepository;
import com.fcs.be.modules.product.service.interfaces.ProductService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ConsignmentItemRepository consignmentItemRepository;
    private final BrandRepository brandRepository;
    private final ProductStatusHistoryRepository productStatusHistoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(
        ProductRepository productRepository,
        ConsignmentItemRepository consignmentItemRepository,
        BrandRepository brandRepository,
        ProductStatusHistoryRepository productStatusHistoryRepository,
        ProductMapper productMapper
    ) {
        this.productRepository = productRepository;
        this.consignmentItemRepository = consignmentItemRepository;
        this.brandRepository = brandRepository;
        this.productStatusHistoryRepository = productStatusHistoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponse> getProducts(ProductStatus status) {
        List<Product> products = status == null
            ? productRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            : productRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(status);
        return products.stream().map(productMapper::toResponse).toList();
    }

    @Override
    public ProductResponse getProduct(UUID id) {
        return productMapper.toResponse(getProductEntity(id));
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        ConsignmentItem consignmentItem = consignmentItemRepository.findByIdAndIsDeletedFalse(request.consignmentItemId())
            .orElseThrow(() -> new EntityNotFoundException("Consignment item not found"));
        product.setConsignmentItem(consignmentItem);
        applyBrand(product, request.brandId());
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setConditionPercent(request.conditionPercent());
        product.setOriginalPrice(request.originalPrice());
        product.setSalePrice(request.salePrice());
        product.setStatus(request.status());
        Product saved = productRepository.save(product);
        appendStatusLog(saved, null, saved.getStatus(), "Product created");
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = getProductEntity(id);
        applyBrand(product, request.brandId());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setConditionPercent(request.conditionPercent());
        product.setOriginalPrice(request.originalPrice());
        product.setSalePrice(request.salePrice());
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateStatus(UUID id, ProductStatus status, String reason) {
        Product product = getProductEntity(id);
        ProductStatus oldStatus = product.getStatus();
        product.setStatus(status);
        Product saved = productRepository.save(product);
        appendStatusLog(saved, oldStatus, status, reason);
        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = getProductEntity(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    private Product getProductEntity(UUID id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private void applyBrand(Product product, UUID brandId) {
        if (brandId == null) {
            product.setBrand(null);
            return;
        }
        Brand brand = brandRepository.findByIdAndIsDeletedFalse(brandId)
            .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
        product.setBrand(brand);
    }

    private void appendStatusLog(Product product, ProductStatus fromStatus, ProductStatus toStatus, String reason) {
        ProductStatusHistory history = new ProductStatusHistory();
        history.setProduct(product);
        history.setFromStatus(fromStatus == null ? null : fromStatus.name());
        history.setToStatus(toStatus.name());
        history.setReason(reason);
        productStatusHistoryRepository.save(history);
    }
}

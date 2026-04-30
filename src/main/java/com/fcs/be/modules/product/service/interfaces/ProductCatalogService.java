package com.fcs.be.modules.product.service.interfaces;

import com.fcs.be.modules.product.dto.request.AssignProductCategoriesRequest;
import com.fcs.be.modules.product.dto.request.CreateWarehouseLogRequest;
import com.fcs.be.modules.product.dto.response.WarehouseLogResponse;
import com.fcs.be.modules.catalog.dto.response.CategoryResponse;
import java.util.List;
import java.util.UUID;

public interface ProductCatalogService {

    List<CategoryResponse> getProductCategories(UUID productId);

    void assignCategories(UUID productId, AssignProductCategoriesRequest request);

    WarehouseLogResponse createWarehouseLog(CreateWarehouseLogRequest request);

    List<WarehouseLogResponse> getWarehouseLogs(UUID productId);
}

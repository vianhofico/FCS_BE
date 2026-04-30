package com.fcs.be.modules.catalog.service.interfaces;

import com.fcs.be.modules.catalog.dto.request.UpsertBrandRequest;
import com.fcs.be.modules.catalog.dto.response.BrandResponse;
import java.util.List;
import java.util.UUID;

public interface BrandService {

    List<BrandResponse> getBrands();

    BrandResponse getBrand(UUID id);

    BrandResponse createBrand(UpsertBrandRequest request);

    BrandResponse updateBrand(UUID id, UpsertBrandRequest request);

    void deleteBrand(UUID id);
}
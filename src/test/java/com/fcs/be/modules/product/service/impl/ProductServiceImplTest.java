package com.fcs.be.modules.product.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.product.dto.request.ProductFilterRequest;
import com.fcs.be.modules.product.dto.response.ProductResponse;
import com.fcs.be.modules.product.service.interfaces.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductServiceImplTest {

    @Autowired
    private ProductService productService;

    @Test
    void testGetProducts_WithNoFilters_ReturnsPage() {
        ProductFilterRequest filter = new ProductFilterRequest(null, null, null, null, null, null, null, null);
        PageResponse<ProductResponse> page = productService.getProducts(filter, PageRequest.of(0, 20));

        assertNotNull(page);
        assertTrue(page.content().size() >= 0);
    }
}

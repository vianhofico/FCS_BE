package com.fcs.be.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fcs.be.common.enums.ConsignmentItemStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.product.dto.request.ProductFilterRequest;
import com.fcs.be.modules.product.dto.response.ProductResponse;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.product.service.interfaces.ProductService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductSearchIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;

    @Autowired
    private ConsignmentItemRepository consignmentItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSearchByKeywordAndStatus() {
        User consignor = User.builder()
            .username("search-user")
            .email("search-user@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(consignor);

        createProduct(consignor, "CONS-PS-001", "SKU-PS-001", "Zara Dress", new BigDecimal("120000"), ProductStatus.SELLING);
        createProduct(consignor, "CONS-PS-002", "SKU-PS-002", "H&M Shirt", new BigDecimal("80000"), ProductStatus.SOLD);

        ProductFilterRequest filter = new ProductFilterRequest(
            "zara",
            null,
            null,
            null,
            null,
            null,
            null,
            ProductStatus.SELLING
        );

        var page = productService.getProducts(filter, PageRequest.of(0, 20));

        assertEquals(1, page.content().size());
        assertTrue(page.content().get(0).name().toLowerCase().contains("zara"));
        assertEquals(ProductStatus.SELLING, page.content().get(0).status());
    }

    @Test
    void testSearchByPriceRange() {
        User consignor = User.builder()
            .username("search-user-2")
            .email("search-user-2@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(consignor);

        createProduct(consignor, "CONS-PS-003", "SKU-PS-003", "Product A", new BigDecimal("100000"), ProductStatus.SELLING);
        createProduct(consignor, "CONS-PS-004", "SKU-PS-004", "Product B", new BigDecimal("300000"), ProductStatus.SELLING);

        ProductFilterRequest filter = new ProductFilterRequest(
            null,
            null,
            null,
            new BigDecimal("50000"),
            new BigDecimal("150000"),
            null,
            null,
            ProductStatus.SELLING
        );

        var page = productService.getProducts(filter, PageRequest.of(0, 20));

        assertEquals(1, page.content().size());
        ProductResponse item = page.content().get(0);
        assertEquals("SKU-PS-003", item.sku());
    }

    private void createProduct(
        User consignor,
        String consignmentCode,
        String sku,
        String name,
        BigDecimal salePrice,
        ProductStatus status
    ) {
        ConsignmentRequest request = ConsignmentRequest.builder()
            .consignor(consignor)
            .code(consignmentCode)
            .status(ConsignmentRequestStatus.APPROVED)
            .build();
        consignmentRequestRepository.save(request);

        ConsignmentItem item = ConsignmentItem.builder()
            .request(request)
            .suggestedName(name)
            .suggestedPrice(salePrice)
            .status(ConsignmentItemStatus.ACCEPTED)
            .build();
        consignmentItemRepository.save(item);

        Product product = Product.builder()
            .consignmentItem(item)
            .sku(sku)
            .name(name)
            .salePrice(salePrice)
            .originalPrice(salePrice)
            .conditionPercent(new BigDecimal("90"))
            .status(status)
            .build();
        productRepository.save(product);
    }
}

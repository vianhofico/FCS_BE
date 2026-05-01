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
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.wishlist.service.interfaces.WishlistService;
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
class WishlistFlowIT {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;

    @Autowired
    private ConsignmentItemRepository consignmentItemRepository;

    @Test
    void testWishlistAddGetRemoveFlow() {
        User user = User.builder()
            .username("wishlist-flow-user")
            .email("wishlist-flow@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(user);

        Product product = createProduct(user, "CONS-WF-001", "SKU-WF-001", "Flow Product", new BigDecimal("150000"));

        wishlistService.addToWishlist(user.getId(), product.getId());

        var page = wishlistService.getWishlist(user.getId(), PageRequest.of(0, 10));
        assertEquals(1, page.content().size());
        assertEquals(product.getId(), page.content().get(0).productId());

        wishlistService.removeFromWishlist(user.getId(), product.getId());

        var afterRemove = wishlistService.getWishlist(user.getId(), PageRequest.of(0, 10));
        assertTrue(afterRemove.content().isEmpty());
    }

    private Product createProduct(User consignor, String consignmentCode, String sku, String name, BigDecimal salePrice) {
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
            .conditionPercent(new BigDecimal("95"))
            .status(ProductStatus.SELLING)
            .build();
        return productRepository.save(product);
    }
}

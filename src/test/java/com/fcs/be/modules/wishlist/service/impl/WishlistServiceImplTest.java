package com.fcs.be.modules.wishlist.service.impl;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.ConsignmentItemStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.wishlist.dto.response.WishlistItemResponse;
import com.fcs.be.modules.wishlist.repository.WishlistItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WishlistServiceImplTest {

    @Autowired
    private WishlistServiceImpl wishlistService;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;

    @Autowired
    private ConsignmentItemRepository consignmentItemRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .username("wishlistuser")
            .email("wishlist@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(testUser);

        ConsignmentRequest request = ConsignmentRequest.builder()
            .consignor(testUser)
            .code("CONS-WISH")
            .status(ConsignmentRequestStatus.APPROVED)
            .build();
        consignmentRequestRepository.save(request);

        ConsignmentItem item = ConsignmentItem.builder()
            .request(request)
            .suggestedName("Wishlist Product")
            .suggestedPrice(new BigDecimal("100000"))
            .status(ConsignmentItemStatus.ACCEPTED)
            .build();
        consignmentItemRepository.save(item);

        testProduct = Product.builder()
            .consignmentItem(item)
            .sku("SKU-WISH")
            .name("Wishlist Product")
            .salePrice(new BigDecimal("100000"))
            .conditionPercent(new BigDecimal("95"))
            .status(ProductStatus.SELLING)
            .build();
        productRepository.save(testProduct);
    }

    @Test
    void testAddToWishlistSuccess() {
        wishlistService.addToWishlist(testUser.getId(), testProduct.getId());

        assertTrue(wishlistItemRepository.existsByUserIdAndProductId(testUser.getId(), testProduct.getId()));
    }

    @Test
    void testAddDuplicateToWishlist() {
        wishlistService.addToWishlist(testUser.getId(), testProduct.getId());

        assertThrows(IllegalStateException.class, () -> wishlistService.addToWishlist(testUser.getId(), testProduct.getId()));
    }

    @Test
    void testRemoveFromWishlistSuccess() {
        wishlistService.addToWishlist(testUser.getId(), testProduct.getId());

        wishlistService.removeFromWishlist(testUser.getId(), testProduct.getId());

        assertFalse(wishlistItemRepository.existsByUserIdAndProductId(testUser.getId(), testProduct.getId()));
    }

    @Test
    void testRemoveNonExistentFromWishlist() {
        assertThrows(EntityNotFoundException.class, () -> wishlistService.removeFromWishlist(testUser.getId(), testProduct.getId()));
    }

    @Test
    void testGetWishlistSuccess() {
        wishlistService.addToWishlist(testUser.getId(), testProduct.getId());

        PageResponse<WishlistItemResponse> response = wishlistService.getWishlist(testUser.getId(), PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(testProduct.getId(), response.content().get(0).productId());
        assertEquals("Wishlist Product", response.content().get(0).productName());
    }
}

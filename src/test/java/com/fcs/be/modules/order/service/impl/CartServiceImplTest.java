package com.fcs.be.modules.order.service.impl;

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
import com.fcs.be.modules.order.dto.request.AddCartItemRequest;
import com.fcs.be.modules.order.dto.response.CartResponse;
import com.fcs.be.modules.order.repository.CartItemRepository;
import com.fcs.be.modules.order.repository.CartRepository;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CartServiceImplTest {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

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
            .username("cartuser")
            .email("cart@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(testUser);

        testProduct = createProduct("SKU001", "Test Product", new BigDecimal("100000"), "CONS-CART-001");
    }

    private Product createProduct(String sku, String name, BigDecimal price, String consignmentCode) {
        ConsignmentRequest request = ConsignmentRequest.builder()
            .consignor(testUser)
            .code(consignmentCode)
            .status(ConsignmentRequestStatus.APPROVED)
            .build();
        consignmentRequestRepository.save(request);

        ConsignmentItem item = ConsignmentItem.builder()
            .request(request)
            .suggestedName(name)
            .suggestedPrice(price)
            .status(ConsignmentItemStatus.ACCEPTED)
            .build();
        consignmentItemRepository.save(item);

        Product product = Product.builder()
            .consignmentItem(item)
            .sku(sku)
            .name(name)
            .salePrice(price)
            .originalPrice(price)
            .conditionPercent(new BigDecimal("90"))
            .status(ProductStatus.SELLING)
            .build();
        productRepository.save(product);
        return product;
    }

    @Test
    void testGetCartSuccess() {
        CartResponse response = cartService.getCart(testUser.getId());

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(testUser.getId(), response.userId());
        assertTrue(response.items().isEmpty());
        assertEquals(BigDecimal.ZERO, response.estimatedTotal());
    }

    @Test
    void testAddItemToCartSuccess() {
        AddCartItemRequest request = new AddCartItemRequest(testProduct.getId());

        CartResponse response = cartService.addItem(testUser.getId(), request);

        assertNotNull(response);
        assertEquals(1, response.items().size());
        assertEquals(0, new BigDecimal("100000").compareTo(response.estimatedTotal()));
    }

    @Test
    void testAddItemWithUnavailableProduct() {
        testProduct.setStatus(ProductStatus.SOLD);
        productRepository.save(testProduct);

        AddCartItemRequest request = new AddCartItemRequest(testProduct.getId());

        assertThrows(IllegalStateException.class, () -> cartService.addItem(testUser.getId(), request));
    }

    @Test
    void testAddDuplicateItemToCart() {
        AddCartItemRequest request = new AddCartItemRequest(testProduct.getId());

        cartService.addItem(testUser.getId(), request);

        assertThrows(IllegalStateException.class, () -> cartService.addItem(testUser.getId(), request));
    }

    @Test
    void testRemoveItemFromCart() {
        AddCartItemRequest request = new AddCartItemRequest(testProduct.getId());
        CartResponse addResponse = cartService.addItem(testUser.getId(), request);

        CartResponse removeResponse = cartService.removeItem(
            testUser.getId(),
            addResponse.items().get(0).id()
        );

        assertTrue(removeResponse.items().isEmpty());
    }

    @Test
    void testClearCart() {
        AddCartItemRequest request = new AddCartItemRequest(testProduct.getId());
        cartService.addItem(testUser.getId(), request);

        cartService.clearCart(testUser.getId());

        CartResponse response = cartService.getCart(testUser.getId());
        assertTrue(response.items().isEmpty());
    }

    @Test
    void testAddMultipleItemsToCart() {
        Product product2 = createProduct("SKU002", "Test Product 2", new BigDecimal("50000"), "CONS-CART-002");

        cartService.addItem(testUser.getId(), new AddCartItemRequest(testProduct.getId()));
        CartResponse response = cartService.addItem(testUser.getId(), new AddCartItemRequest(product2.getId()));

        assertEquals(2, response.items().size());
        assertEquals(0, new BigDecimal("150000").compareTo(response.estimatedTotal()));
    }
}

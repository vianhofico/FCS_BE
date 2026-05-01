package com.fcs.be.modules.review.service.impl;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.ConsignmentItemStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import com.fcs.be.modules.order.repository.OrderItemRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.review.dto.request.CreateReviewRequest;
import com.fcs.be.modules.review.dto.response.ProductReviewResponse;
import com.fcs.be.modules.review.dto.response.ReviewSummaryResponse;
import com.fcs.be.modules.review.repository.ProductReviewRepository;
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
class ProductReviewServiceImplTest {

    @Autowired
    private ProductReviewServiceImpl reviewService;

    @Autowired
    private ProductReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;

    @Autowired
    private ConsignmentItemRepository consignmentItemRepository;

    private User buyerUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        buyerUser = User.builder()
            .username("buyeruser")
            .email("buyer@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(buyerUser);

        ConsignmentRequest request = ConsignmentRequest.builder()
            .consignor(buyerUser) // Just reusing the user for setup speed
            .code("CONS-REV")
            .status(ConsignmentRequestStatus.APPROVED)
            .build();
        consignmentRequestRepository.save(request);

        ConsignmentItem item = ConsignmentItem.builder()
            .request(request)
            .suggestedName("Review Product")
            .suggestedPrice(new BigDecimal("100000"))
            .status(ConsignmentItemStatus.ACCEPTED)
            .build();
        consignmentItemRepository.save(item);

        testProduct = Product.builder()
            .consignmentItem(item)
            .sku("SKU-REV")
            .name("Review Product")
            .salePrice(new BigDecimal("100000"))
            .conditionPercent(new BigDecimal("95"))
            .status(ProductStatus.SELLING)
            .build();
        productRepository.save(testProduct);

        testOrder = Order.builder()
            .buyer(buyerUser)
            .orderCode("ORD-REV-001")
            .subTotal(new BigDecimal("100000"))
            .shippingFee(new BigDecimal("0"))
            .discountAmount(new BigDecimal("0"))
            .totalAmount(new BigDecimal("100000"))
            .status(OrderStatus.COMPLETED)
            .build();
        orderRepository.save(testOrder);

        OrderItem orderItem = OrderItem.builder()
            .order(testOrder)
            .product(testProduct)
            .skuSnapshot("SKU-REV")
            .productNameSnapshot("Review Product")
            .priceAtPurchase(new BigDecimal("100000"))
            .build();
        orderItemRepository.save(orderItem);
    }

    @Test
    void testCreateReviewSuccess() {
        CreateReviewRequest request = new CreateReviewRequest(testProduct.getId(), 5, "Great product!");

        ProductReviewResponse response = reviewService.createReview(request, buyerUser.getId());

        assertNotNull(response);
        assertEquals(5, response.rating());
        assertEquals("Great product!", response.comment());
        assertTrue(response.verifiedPurchase());
    }

    @Test
    void testCreateReviewNotPurchased() {
        // Change order status so it's not COMPLETED
        testOrder.setStatus(OrderStatus.PACKING);
        orderRepository.save(testOrder);

        CreateReviewRequest request = new CreateReviewRequest(testProduct.getId(), 5, "Great product!");

        assertThrows(IllegalStateException.class, () -> reviewService.createReview(request, buyerUser.getId()));
    }

    @Test
    void testCreateReviewDuplicate() {
        CreateReviewRequest request = new CreateReviewRequest(testProduct.getId(), 5, "Great product!");
        reviewService.createReview(request, buyerUser.getId());

        assertThrows(IllegalStateException.class, () -> reviewService.createReview(request, buyerUser.getId()));
    }

    @Test
    void testGetReviews() {
        CreateReviewRequest request = new CreateReviewRequest(testProduct.getId(), 4, "Good product");
        reviewService.createReview(request, buyerUser.getId());

        PageResponse<ProductReviewResponse> response = reviewService.getProductReviews(testProduct.getId(), PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(1, response.content().size());
        assertEquals(4, response.content().get(0).rating());
    }

    @Test
    void testGetReviewSummary() {
        CreateReviewRequest request = new CreateReviewRequest(testProduct.getId(), 5, "Excellent");
        reviewService.createReview(request, buyerUser.getId());

        ReviewSummaryResponse summary = reviewService.getProductReviewSummary(testProduct.getId());

        assertNotNull(summary);
        assertEquals(1, summary.totalReviews());
        assertEquals(5.0, summary.averageRating(), 0.01);
        assertEquals(1, summary.fiveStarCount());
    }
}

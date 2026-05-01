package com.fcs.be.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fcs.be.common.enums.ConsignmentItemStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import com.fcs.be.modules.order.repository.OrderItemRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.review.dto.request.CreateReviewRequest;
import com.fcs.be.modules.review.service.interfaces.ProductReviewService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReviewFlowIT {

    @Autowired
    private ProductReviewService productReviewService;

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

    @Test
    void testReviewAfterCompletedPurchaseFlow() {
        User buyer = User.builder()
            .username("review-buyer")
            .email("review-buyer@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(buyer);

        Product product = createProduct(buyer, "CONS-RV-001", "SKU-RV-001", "Review Product", new BigDecimal("220000"));

        Order order = Order.builder()
            .buyer(buyer)
            .orderCode("ORD-RV-001")
            .subTotal(product.getSalePrice())
            .shippingFee(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(product.getSalePrice())
            .status(OrderStatus.COMPLETED)
            .build();
        orderRepository.save(order);

        OrderItem item = OrderItem.builder()
            .order(order)
            .product(product)
            .skuSnapshot(product.getSku())
            .productNameSnapshot(product.getName())
            .conditionSnapshot(product.getConditionPercent().toPlainString())
            .priceAtPurchase(product.getSalePrice())
            .build();
        orderItemRepository.save(item);

        CreateReviewRequest createReviewRequest = new CreateReviewRequest(product.getId(), 5, "Excellent condition");
        var created = productReviewService.createReview(createReviewRequest, buyer.getId());

        assertNotNull(created.id());
        assertEquals(5, created.rating());
        assertEquals(true, created.verifiedPurchase());

        var summary = productReviewService.getProductReviewSummary(product.getId());
        assertEquals(1L, summary.totalReviews());
        assertEquals(5.0, summary.averageRating());
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
            .conditionPercent(new BigDecimal("96"))
            .status(ProductStatus.SELLING)
            .build();
        return productRepository.save(product);
    }
}

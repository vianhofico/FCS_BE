package com.fcs.be.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fcs.be.common.enums.ConsignmentItemStatus;
import com.fcs.be.common.enums.ConsignmentRequestStatus;
import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.ReturnRequestStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.common.enums.WalletTransactionType;
import com.fcs.be.modules.consignment.entity.ConsignmentItem;
import com.fcs.be.modules.consignment.entity.ConsignmentRequest;
import com.fcs.be.modules.consignment.repository.ConsignmentItemRepository;
import com.fcs.be.modules.consignment.repository.ConsignmentRequestRepository;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.financial.repository.WalletTransactionRepository;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import com.fcs.be.modules.order.repository.OrderItemRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.return_request.dto.request.CreateReturnRequestRequest;
import com.fcs.be.modules.return_request.dto.request.UpdateReturnStatusRequest;
import com.fcs.be.modules.return_request.service.interfaces.ReturnRequestService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReturnRefundFlowIT {

    @Autowired
    private ReturnRequestService returnRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConsignmentRequestRepository consignmentRequestRepository;

    @Autowired
    private ConsignmentItemRepository consignmentItemRepository;

    @Test
    void testReturnToRefundFlow() {
        User buyer = User.builder()
            .username("return-buyer")
            .email("return-buyer@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(buyer);

        User manager = User.builder()
            .username("return-manager")
            .email("return-manager@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(manager);

        Wallet wallet = Wallet.builder()
            .user(buyer)
            .balance(BigDecimal.ZERO)
            .availableBalance(BigDecimal.ZERO)
            .build();
        walletRepository.save(wallet);

        Product product = createProduct(buyer, "CONS-RET-001", "SKU-RET-001", "Returnable Product", new BigDecimal("300000"));

        Order order = Order.builder()
            .buyer(buyer)
            .orderCode("ORD-RET-INT-001")
            .subTotal(product.getSalePrice())
            .shippingFee(BigDecimal.ZERO)
            .discountAmount(BigDecimal.ZERO)
            .totalAmount(product.getSalePrice())
            .status(OrderStatus.COMPLETED)
            .build();
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
            .order(order)
            .product(product)
            .skuSnapshot(product.getSku())
            .productNameSnapshot(product.getName())
            .conditionSnapshot(product.getConditionPercent().toPlainString())
            .priceAtPurchase(product.getSalePrice())
            .build();
        orderItemRepository.save(orderItem);

        var created = returnRequestService.createReturnRequest(
            new CreateReturnRequestRequest(order.getId(), "Wrong size", "http://evidence-1"),
            buyer.getId()
        );
        assertEquals(ReturnRequestStatus.PENDING, created.status());

        var refunded = returnRequestService.updateStatus(
            created.id(),
            new UpdateReturnStatusRequest(ReturnRequestStatus.REFUNDED, "Refund approved"),
            manager.getId()
        );

        assertEquals(ReturnRequestStatus.REFUNDED, refunded.status());
        assertEquals(OrderStatus.REFUNDED, orderRepository.findById(order.getId()).orElseThrow().getStatus());
        assertEquals(ProductStatus.RETURNED, productRepository.findById(product.getId()).orElseThrow().getStatus());

        var txs = walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
        assertTrue(txs.stream().anyMatch(tx -> tx.getType() == WalletTransactionType.REFUND && tx.getAmount().compareTo(order.getTotalAmount()) == 0));
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
            .conditionPercent(new BigDecimal("92"))
            .status(ProductStatus.SELLING)
            .build();
        return productRepository.save(product);
    }
}

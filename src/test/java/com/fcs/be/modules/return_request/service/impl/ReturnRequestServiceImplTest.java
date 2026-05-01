package com.fcs.be.modules.return_request.service.impl;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.ReturnRequestStatus;
import com.fcs.be.common.enums.UserStatus;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.return_request.dto.request.CreateReturnRequestRequest;
import com.fcs.be.modules.return_request.dto.request.ReturnFilterRequest;
import com.fcs.be.modules.return_request.dto.request.UpdateReturnStatusRequest;
import com.fcs.be.modules.return_request.dto.response.ReturnRequestResponse;
import com.fcs.be.modules.return_request.repository.ReturnRequestRepository;
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
class ReturnRequestServiceImplTest {

    @Autowired
    private ReturnRequestServiceImpl returnService;

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletRepository walletRepository;

    private User buyerUser;
    private User managerUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        buyerUser = User.builder()
            .username("buyer")
            .email("buyer@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(buyerUser);

        managerUser = User.builder()
            .username("manager")
            .email("manager@example.com")
            .passwordHash("hashed")
            .status(UserStatus.ACTIVE)
            .build();
        userRepository.save(managerUser);

        Wallet wallet = Wallet.builder()
            .user(buyerUser)
            .balance(BigDecimal.ZERO)
            .availableBalance(BigDecimal.ZERO)
            .build();
        walletRepository.save(wallet);

        testOrder = Order.builder()
            .buyer(buyerUser)
            .orderCode("ORD-RET-001")
            .subTotal(new BigDecimal("100000"))
            .shippingFee(new BigDecimal("0"))
            .discountAmount(new BigDecimal("0"))
            .totalAmount(new BigDecimal("100000"))
            .status(OrderStatus.COMPLETED)
            .build();
        orderRepository.save(testOrder);
    }

    @Test
    void testCreateReturnRequestSuccess() {
        CreateReturnRequestRequest request = new CreateReturnRequestRequest(testOrder.getId(), "Defective item", "http://evidence.com");

        ReturnRequestResponse response = returnService.createReturnRequest(request, buyerUser.getId());

        assertNotNull(response);
        assertEquals("Defective item", response.reason());
        assertEquals(ReturnRequestStatus.PENDING, response.status());
    }

    @Test
    void testCreateReturnRequestNotCompletedOrder() {
        testOrder.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(testOrder);

        CreateReturnRequestRequest request = new CreateReturnRequestRequest(testOrder.getId(), "Defective item", null);

        assertThrows(IllegalStateException.class, () -> returnService.createReturnRequest(request, buyerUser.getId()));
    }

    @Test
    void testUpdateStatusToApproved() {
        CreateReturnRequestRequest createRequest = new CreateReturnRequestRequest(testOrder.getId(), "Defective item", null);
        ReturnRequestResponse created = returnService.createReturnRequest(createRequest, buyerUser.getId());

        UpdateReturnStatusRequest updateRequest = new UpdateReturnStatusRequest(ReturnRequestStatus.APPROVED, "Please return the item");
        ReturnRequestResponse updated = returnService.updateStatus(created.id(), updateRequest, managerUser.getId());

        assertEquals(ReturnRequestStatus.APPROVED, updated.status());
        assertEquals("Please return the item", updated.reviewNote());
        assertEquals(managerUser.getId(), updated.reviewedById());
    }

    @Test
    void testUpdateStatusToRefunded() {
        CreateReturnRequestRequest createRequest = new CreateReturnRequestRequest(testOrder.getId(), "Defective item", null);
        ReturnRequestResponse created = returnService.createReturnRequest(createRequest, buyerUser.getId());

        UpdateReturnStatusRequest updateRequest = new UpdateReturnStatusRequest(ReturnRequestStatus.REFUNDED, "Refund processed");
        ReturnRequestResponse updated = returnService.updateStatus(created.id(), updateRequest, managerUser.getId());

        assertEquals(ReturnRequestStatus.REFUNDED, updated.status());

        // Verify order status changed to REFUNDED
        Order updatedOrder = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.REFUNDED, updatedOrder.getStatus());
    }

    @Test
    void testGetReturnRequests() {
        CreateReturnRequestRequest createRequest = new CreateReturnRequestRequest(testOrder.getId(), "Defective item", null);
        returnService.createReturnRequest(createRequest, buyerUser.getId());

        ReturnFilterRequest filter = new ReturnFilterRequest(null, null, ReturnRequestStatus.PENDING, null, null);
        var page = returnService.getReturnRequests(filter, PageRequest.of(0, 10));

        assertEquals(1, page.content().size());
        assertEquals(ReturnRequestStatus.PENDING, page.content().get(0).status());
    }
}

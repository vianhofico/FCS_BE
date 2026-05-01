package com.fcs.be.modules.return_request.service.impl;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.ReturnRequestStatus;
import com.fcs.be.common.enums.WalletTransactionType;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.financial.service.interfaces.WalletService;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import com.fcs.be.modules.order.repository.OrderItemRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.order.service.interfaces.OrderService;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.return_request.dto.request.CreateReturnRequestRequest;
import com.fcs.be.modules.return_request.dto.request.ReturnFilterRequest;
import com.fcs.be.modules.return_request.dto.request.UpdateReturnStatusRequest;
import com.fcs.be.modules.return_request.dto.response.ReturnRequestResponse;
import com.fcs.be.modules.return_request.entity.ReturnRequest;
import com.fcs.be.modules.return_request.entity.ReturnStatusHistory;
import com.fcs.be.modules.return_request.mapper.ReturnRequestMapper;
import com.fcs.be.modules.return_request.repository.ReturnRequestRepository;
import com.fcs.be.modules.return_request.repository.ReturnSpecification;
import com.fcs.be.modules.return_request.repository.ReturnStatusHistoryRepository;
import com.fcs.be.modules.return_request.service.interfaces.ReturnRequestService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReturnRequestServiceImpl implements ReturnRequestService {

    private final ReturnRequestRepository returnRequestRepository;
    private final ReturnStatusHistoryRepository returnStatusHistoryRepository;
    private final ReturnRequestMapper returnRequestMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final WalletRepository walletRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public ReturnRequestServiceImpl(
        ReturnRequestRepository returnRequestRepository,
        ReturnStatusHistoryRepository returnStatusHistoryRepository,
        ReturnRequestMapper returnRequestMapper,
        OrderRepository orderRepository,
        UserRepository userRepository,
        WalletService walletService,
        WalletRepository walletRepository,
        OrderService orderService,
        ProductRepository productRepository,
        OrderItemRepository orderItemRepository
    ) {
        this.returnRequestRepository = returnRequestRepository;
        this.returnStatusHistoryRepository = returnStatusHistoryRepository;
        this.returnRequestMapper = returnRequestMapper;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.orderService = orderService;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public PageResponse<ReturnRequestResponse> getReturnRequests(ReturnFilterRequest filter, Pageable pageable) {
        return PageResponse.of(
            returnRequestRepository.findAll(ReturnSpecification.from(filter), pageable)
                .map(returnRequestMapper::toResponse)
        );
    }

    @Override
    public ReturnRequestResponse getReturnRequest(UUID id) {
        return returnRequestMapper.toResponse(getReturnRequestEntity(id));
    }

    @Override
    @Transactional
    public ReturnRequestResponse createReturnRequest(CreateReturnRequestRequest request, UUID requestedById) {
        Order order = orderRepository.findByIdAndIsDeletedFalse(request.orderId())
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Return can only be requested for COMPLETED orders");
        }

        User user = userRepository.findByIdAndIsDeletedFalse(requestedById)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ReturnRequest entity = ReturnRequest.builder()
            .order(order)
            .requestedBy(user)
            .reason(request.reason())
            .evidenceUrls(request.evidenceUrls())
            .status(ReturnRequestStatus.PENDING)
            .build();

        ReturnRequest saved = returnRequestRepository.save(entity);
        appendStatusLog(saved, null, ReturnRequestStatus.PENDING, "Created return request");
        return returnRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReturnRequestResponse updateStatus(UUID id, UpdateReturnStatusRequest request, UUID reviewerId) {
        ReturnRequest returnRequest = getReturnRequestEntity(id);
        ReturnRequestStatus oldStatus = returnRequest.getStatus();

        if (oldStatus == ReturnRequestStatus.REFUNDED || oldStatus == ReturnRequestStatus.REJECTED) {
            throw new IllegalStateException("Cannot change status of a completed return request");
        }

        User reviewer = null;
        if (reviewerId != null) {
            reviewer = userRepository.findByIdAndIsDeletedFalse(reviewerId)
                .orElseThrow(() -> new EntityNotFoundException("Reviewer not found"));
            returnRequest.setReviewedBy(reviewer);
            returnRequest.setReviewedAt(Instant.now());
        }

        returnRequest.setStatus(request.status());
        if (request.reason() != null) {
            returnRequest.setReviewNote(request.reason());
        }

        if (request.status() == ReturnRequestStatus.REFUNDED) {
            processRefund(returnRequest);
        }

        ReturnRequest saved = returnRequestRepository.save(returnRequest);
        appendStatusLog(saved, oldStatus, request.status(), request.reason());
        return returnRequestMapper.toResponse(saved);
    }

    private ReturnRequest getReturnRequestEntity(UUID id) {
        return returnRequestRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Return request not found"));
    }

    private void processRefund(ReturnRequest returnRequest) {
        Order order = returnRequest.getOrder();
        User buyer = order.getBuyer();

        Wallet buyerWallet = walletRepository.findByUserIdAndIsDeletedFalse(buyer.getId())
            .orElseThrow(() -> new EntityNotFoundException("Buyer wallet not found"));

        walletService.recordTransaction(
            buyerWallet.getId(),
            WalletTransactionType.REFUND,
            order.getTotalAmount(),
            "Refund for order " + order.getOrderCode(),
            "RETURN_REQUEST",
            returnRequest.getId()
        );

        orderService.updateStatus(order.getId(), OrderStatus.REFUNDED, "Order refunded due to return request");

        List<OrderItem> items = orderItemRepository.findByOrderIdAndIsDeletedFalse(order.getId());
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStatus(ProductStatus.RETURNED);
            productRepository.save(product);
        }
    }

    private void appendStatusLog(ReturnRequest request, ReturnRequestStatus from, ReturnRequestStatus to, String reason) {
        ReturnStatusHistory history = ReturnStatusHistory.builder()
            .returnRequest(request)
            .fromStatus(from == null ? null : from.name())
            .toStatus(to.name())
            .reason(reason)
            .build();
        returnStatusHistoryRepository.save(history);
    }
}

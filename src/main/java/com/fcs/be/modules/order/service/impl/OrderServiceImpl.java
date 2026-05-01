package com.fcs.be.modules.order.service.impl;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.common.enums.WalletTransactionType;
import com.fcs.be.modules.consignment.entity.ConsignmentContract;
import com.fcs.be.modules.consignment.repository.ConsignmentContractRepository;
import com.fcs.be.modules.financial.entity.Wallet;
import com.fcs.be.modules.financial.repository.WalletRepository;
import com.fcs.be.modules.financial.service.interfaces.WalletService;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.entity.UserAddress;
import com.fcs.be.modules.iam.repository.UserAddressRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.order.dto.request.CreateOrderRequest;
import com.fcs.be.modules.order.dto.request.OrderFilterRequest;
import com.fcs.be.modules.order.repository.OrderSpecification;
import com.fcs.be.modules.order.dto.response.OrderResponse;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.entity.OrderItem;
import com.fcs.be.modules.order.entity.OrderStatusHistory;
import com.fcs.be.modules.order.mapper.OrderMapper;
import com.fcs.be.modules.order.repository.OrderItemRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.order.repository.OrderStatusHistoryRepository;
import com.fcs.be.modules.order.service.interfaces.OrderService;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fcs.be.modules.order.dto.request.UpdateOrderTrackingRequest;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final ConsignmentContractRepository consignmentContractRepository;
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        UserRepository userRepository,
        UserAddressRepository userAddressRepository,
        ProductRepository productRepository,
        OrderMapper orderMapper,
        ConsignmentContractRepository consignmentContractRepository,
        WalletService walletService,
        WalletRepository walletRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.consignmentContractRepository = consignmentContractRepository;
        this.walletService = walletService;
        this.walletRepository = walletRepository;
    }

    @Override
    public PageResponse<OrderResponse> getOrders(OrderFilterRequest filter, Pageable pageable) {
        return PageResponse.of(
            orderRepository.findAll(OrderSpecification.from(filter), pageable)
                .map(order -> orderMapper.toResponse(order, orderItemRepository.findByOrderIdAndIsDeletedFalse(order.getId())))
        );
    }

    @Override
    public OrderResponse getOrder(UUID id) {
        Order order = getOrderEntity(id);
        return orderMapper.toResponse(order, orderItemRepository.findByOrderIdAndIsDeletedFalse(order.getId()));
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User buyer = userRepository.findByIdAndIsDeletedFalse(request.buyerId())
            .orElseThrow(() -> new EntityNotFoundException("Buyer not found"));
        Order order = Order.builder()
            .buyer(buyer)
            .orderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .subTotal(request.subTotal())
            .shippingFee(request.shippingFee())
            .discountAmount(request.discountAmount())
            .totalAmount(request.totalAmount())
            .paymentMethod(request.paymentMethod())
            .shippingSnapshot(request.shippingSnapshot())
            .status(OrderStatus.PENDING_PAYMENT)
            .shippingAddress(request.shippingAddressId() != null
                ? userAddressRepository.findByIdAndIsDeletedFalse(request.shippingAddressId())
                    .orElseThrow(() -> new EntityNotFoundException("Shipping address not found"))
                : null)
            .build();
        Order savedOrder = orderRepository.save(order);

        for (UUID productId : request.productIds()) {
            Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

            if (product.getStatus() != ProductStatus.SELLING) {
                throw new IllegalStateException("Product is not available for purchase: " + product.getSku());
            }

            product.setStatus(ProductStatus.RESERVED);
            product.setReservedUntil(Instant.now().plus(30, ChronoUnit.MINUTES));
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                .order(savedOrder)
                .product(product)
                .skuSnapshot(product.getSku())
                .productNameSnapshot(product.getName())
                .conditionSnapshot(product.getConditionPercent().toPlainString())
                .priceAtPurchase(product.getSalePrice())
                .build();
            orderItemRepository.save(item);
        }

        appendStatusLog(savedOrder, null, OrderStatus.PENDING_PAYMENT, "Order created");
        return orderMapper.toResponse(savedOrder, orderItemRepository.findByOrderIdAndIsDeletedFalse(savedOrder.getId()));
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID id, OrderStatus status, String reason) {
        Order order = getOrderEntity(id);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);

        if (status == OrderStatus.COMPLETED && oldStatus != OrderStatus.COMPLETED) {
            handleOrderCompletion(order);
        } else if ((status == OrderStatus.CANCELLED || status == OrderStatus.REFUNDED)
                && (oldStatus != OrderStatus.CANCELLED && oldStatus != OrderStatus.REFUNDED)) {
            handleOrderCancellation(order);
        }

        Order saved = orderRepository.save(order);
        appendStatusLog(saved, oldStatus, status, reason);
        return orderMapper.toResponse(saved, orderItemRepository.findByOrderIdAndIsDeletedFalse(saved.getId()));
    }

    @Transactional
    private void handleOrderCompletion(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderIdAndIsDeletedFalse(order.getId());
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStatus(ProductStatus.SOLD);
            product.setReservedUntil(null);
            productRepository.save(product);

            // Calculate revenue and pay consignor
            ConsignmentContract contract = consignmentContractRepository
                .findByRequestIdAndIsDeletedFalse(product.getConsignmentItem().getRequest().getId())
                .orElse(null);

            if (contract != null) {
                // Commission rate is in decimal (e.g. 0.20 for 20%)
                BigDecimal commission = item.getPriceAtPurchase().multiply(contract.getCommissionRate());
                BigDecimal consignorRevenue = item.getPriceAtPurchase().subtract(commission);

                User consignor = product.getConsignmentItem().getRequest().getConsignor();
                Wallet wallet = walletRepository.findByUserIdAndIsDeletedFalse(consignor.getId())
                    .orElseThrow(() -> new IllegalStateException("Consignor wallet not found"));

                walletService.recordTransaction(
                    wallet.getId(),
                    WalletTransactionType.SALE_REVENUE,
                    consignorRevenue,
                    "Revenue from sale of product " + product.getSku(),
                    "ORDER_ITEM",
                    item.getId()
                );
            }
        }
    }

    private void handleOrderCancellation(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderIdAndIsDeletedFalse(order.getId());
        for (OrderItem item : items) {
            Product product = item.getProduct();
            if (product.getStatus() == ProductStatus.RESERVED || product.getStatus() == ProductStatus.SOLD) {
                product.setStatus(ProductStatus.SELLING);
                product.setReservedUntil(null);
                productRepository.save(product);
            }
        }
    }

    @Override
    @Transactional
    public OrderResponse updateTracking(UUID id, UpdateOrderTrackingRequest request) {
        Order order = getOrderEntity(id);
        order.setTrackingNumber(request.trackingNumber());
        order.setShippingProvider(request.shippingProvider());
        if (order.getStatus() == OrderStatus.PACKING) {
            order.setStatus(OrderStatus.SHIPPED);
            appendStatusLog(order, OrderStatus.PACKING, OrderStatus.SHIPPED, "Updated tracking number");
        }
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved, orderItemRepository.findByOrderIdAndIsDeletedFalse(saved.getId()));
    }

    @Override
    @Transactional
    public void deleteOrder(UUID id) {
        Order order = getOrderEntity(id);
        order.setDeleted(true);
        orderRepository.save(order);
    }

    private Order getOrderEntity(UUID id) {
        return orderRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Transactional
    private void appendStatusLog(Order order, OrderStatus fromStatus, OrderStatus toStatus, String reason) {
        OrderStatusHistory history = OrderStatusHistory.builder()
            .order(order)
            .fromStatus(fromStatus == null ? null : fromStatus.name())
            .toStatus(toStatus.name())
            .reason(reason)
            .build();
        orderStatusHistoryRepository.save(history);
    }
}

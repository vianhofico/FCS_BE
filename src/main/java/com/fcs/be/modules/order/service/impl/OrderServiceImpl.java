package com.fcs.be.modules.order.service.impl;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.entity.UserAddress;
import com.fcs.be.modules.iam.repository.UserAddressRepository;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.dto.request.CreateOrderRequest;
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
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        OrderStatusHistoryRepository orderStatusHistoryRepository,
        UserRepository userRepository,
        UserAddressRepository userAddressRepository,
        ProductRepository productRepository,
        OrderMapper orderMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.userRepository = userRepository;
        this.userAddressRepository = userAddressRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public List<OrderResponse> getOrders(OrderStatus status) {
        List<Order> orders = status == null
            ? orderRepository.findByIsDeletedFalseOrderByCreatedAtDesc()
            : orderRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(status);
        return orders.stream().map(this::toResponse).toList();
    }

    @Override
    public OrderResponse getOrder(UUID id) {
        return toResponse(getOrderEntity(id));
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User buyer = userRepository.findByIdAndIsDeletedFalse(request.buyerId())
            .orElseThrow(() -> new EntityNotFoundException("Buyer not found"));
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setSubTotal(request.subTotal());
        order.setShippingFee(request.shippingFee());
        order.setDiscountAmount(request.discountAmount());
        order.setTotalAmount(request.totalAmount());
        order.setPaymentMethod(request.paymentMethod());
        order.setShippingSnapshot(request.shippingSnapshot());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        if (request.shippingAddressId() != null) {
            UserAddress address = userAddressRepository.findByIdAndIsDeletedFalse(request.shippingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Shipping address not found"));
            order.setShippingAddress(address);
        }
        Order savedOrder = orderRepository.save(order);

        for (UUID productId : request.productIds()) {
            Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setSkuSnapshot(product.getSku());
            item.setProductNameSnapshot(product.getName());
            item.setConditionSnapshot(product.getConditionPercent().toPlainString());
            item.setPriceAtPurchase(product.getSalePrice());
            orderItemRepository.save(item);
        }

        appendStatusLog(savedOrder, null, OrderStatus.PENDING_PAYMENT, "Order created");
        return toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID id, OrderStatus status, String reason) {
        Order order = getOrderEntity(id);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        appendStatusLog(saved, oldStatus, status, reason);
        return toResponse(saved);
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

    private OrderResponse toResponse(Order order) {
        return orderMapper.toResponse(order, orderItemRepository.findByOrderIdAndIsDeletedFalse(order.getId()));
    }

    private void appendStatusLog(Order order, OrderStatus fromStatus, OrderStatus toStatus, String reason) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setFromStatus(fromStatus == null ? null : fromStatus.name());
        history.setToStatus(toStatus.name());
        history.setReason(reason);
        orderStatusHistoryRepository.save(history);
    }
}

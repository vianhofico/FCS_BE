package com.fcs.be.modules.review.service.impl;

import com.fcs.be.common.enums.OrderStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.entity.Order;
import com.fcs.be.modules.order.repository.OrderItemRepository;
import com.fcs.be.modules.order.repository.OrderRepository;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.review.dto.request.CreateReviewRequest;
import com.fcs.be.modules.review.dto.response.ProductReviewResponse;
import com.fcs.be.modules.review.dto.response.ReviewSummaryResponse;
import com.fcs.be.modules.review.entity.ProductReview;
import com.fcs.be.modules.review.mapper.ProductReviewMapper;
import com.fcs.be.modules.review.repository.ProductReviewRepository;
import com.fcs.be.modules.review.service.interfaces.ProductReviewService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductReviewMapper productReviewMapper;

    public ProductReviewServiceImpl(
        ProductReviewRepository productReviewRepository,
        ProductRepository productRepository,
        UserRepository userRepository,
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        ProductReviewMapper productReviewMapper
    ) {
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productReviewMapper = productReviewMapper;
    }

    @Override
    public PageResponse<ProductReviewResponse> getProductReviews(UUID productId, Pageable pageable) {
        return PageResponse.of(
            productReviewRepository.findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(productId, pageable)
                .map(productReviewMapper::toResponse)
        );
    }

    @Override
    public ReviewSummaryResponse getProductReviewSummary(UUID productId) {
        List<Object[]> results = productReviewRepository.getReviewSummaryByProductId(productId);
        long total = 0;
        long five = 0, four = 0, three = 0, two = 0, one = 0;
        double sum = 0;

        for (Object[] result : results) {
            int rating = (Integer) result[0];
            long count = (Long) result[1];
            total += count;
            sum += (rating * count);

            switch (rating) {
                case 5 -> five = count;
                case 4 -> four = count;
                case 3 -> three = count;
                case 2 -> two = count;
                case 1 -> one = count;
            }
        }

        double avg = total == 0 ? 0.0 : Math.round((sum / total) * 10.0) / 10.0;
        return new ReviewSummaryResponse(avg, total, five, four, three, two, one);
    }

    @Override
    @Transactional
    public ProductReviewResponse createReview(CreateReviewRequest request, UUID buyerId) {
        if (productReviewRepository.existsByProductIdAndBuyerIdAndIsDeletedFalse(request.productId(), buyerId)) {
            throw new IllegalStateException("You have already reviewed this product");
        }

        Product product = productRepository.findByIdAndIsDeletedFalse(request.productId())
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        User buyer = userRepository.findByIdAndIsDeletedFalse(buyerId)
            .orElseThrow(() -> new EntityNotFoundException("Buyer not found"));

        boolean hasPurchased = orderRepository.findByIsDeletedFalseAndStatusOrderByCreatedAtDesc(OrderStatus.COMPLETED)
            .stream()
            .filter(o -> o.getBuyer().getId().equals(buyerId))
            .anyMatch(o -> orderItemRepository.existsByOrderIdAndProductIdAndIsDeletedFalse(o.getId(), request.productId()));

        if (!hasPurchased) {
            throw new IllegalStateException("You can only review products you have purchased and completed");
        }

        ProductReview review = ProductReview.builder()
            .product(product)
            .buyer(buyer)
            .rating(request.rating())
            .comment(request.comment())
            .verifiedPurchase(true)
            .build();

        return productReviewMapper.toResponse(productReviewRepository.save(review));
    }
}

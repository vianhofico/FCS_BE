package com.fcs.be.modules.wishlist.service.impl;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import com.fcs.be.modules.wishlist.dto.response.WishlistItemResponse;
import com.fcs.be.modules.wishlist.entity.WishlistItem;
import com.fcs.be.modules.wishlist.mapper.WishlistItemMapper;
import com.fcs.be.modules.wishlist.repository.WishlistItemRepository;
import com.fcs.be.modules.wishlist.service.interfaces.WishlistService;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistItemMapper wishlistMapper;

    public WishlistServiceImpl(
        WishlistItemRepository wishlistRepository,
        UserRepository userRepository,
        ProductRepository productRepository,
        WishlistItemMapper wishlistMapper
    ) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.wishlistMapper = wishlistMapper;
    }

    @Override
    public PageResponse<WishlistItemResponse> getWishlist(UUID userId, Pageable pageable) {
        return PageResponse.of(
            wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(wishlistMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public void addToWishlist(UUID userId, UUID productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("Product is already in your wishlist");
        }

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        WishlistItem item = WishlistItem.builder()
            .user(user)
            .product(product)
            .build();

        wishlistRepository.save(item);
    }

    @Override
    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        WishlistItem item = wishlistRepository.findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new EntityNotFoundException("Product not found in wishlist"));
        wishlistRepository.delete(item);
    }
}

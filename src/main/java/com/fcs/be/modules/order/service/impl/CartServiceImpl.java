package com.fcs.be.modules.order.service.impl;

import com.fcs.be.common.enums.ProductStatus;
import com.fcs.be.modules.iam.entity.User;
import com.fcs.be.modules.iam.repository.UserRepository;
import com.fcs.be.modules.order.dto.request.AddCartItemRequest;
import com.fcs.be.modules.order.dto.response.CartResponse;
import com.fcs.be.modules.order.entity.Cart;
import com.fcs.be.modules.order.entity.CartItem;
import com.fcs.be.modules.order.mapper.CartMapper;
import com.fcs.be.modules.order.repository.CartItemRepository;
import com.fcs.be.modules.order.repository.CartRepository;
import com.fcs.be.modules.order.service.interfaces.CartService;
import com.fcs.be.modules.product.entity.Product;
import com.fcs.be.modules.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(
        CartRepository cartRepository,
        CartItemRepository cartItemRepository,
        UserRepository userRepository,
        ProductRepository productRepository,
        CartMapper cartMapper
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional
    public CartResponse getCart(UUID userId) {
        Cart cart = findOrCreateCart(userId);
        return cartMapper.toResponse(cart, cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId()));
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID userId, AddCartItemRequest request) {
        Cart cart = findOrCreateCart(userId);

        Product product = productRepository.findByIdAndIsDeletedFalse(request.productId())
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getStatus() != ProductStatus.SELLING) {
            throw new IllegalStateException("Product is not available for purchase");
        }

        if (cartItemRepository.findByCartIdAndProductIdAndIsDeletedFalse(cart.getId(), product.getId()).isPresent()) {
            throw new IllegalStateException("Product is already in the cart");
        }

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        cartItemRepository.save(item);

        return cartMapper.toResponse(cart, cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId()));
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID userId, UUID cartItemId) {
        Cart cart = findOrCreateCart(userId);
        CartItem item = cartItemRepository.findByIdAndIsDeletedFalse(cartItemId)
            .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalStateException("Cart item does not belong to this user");
        }

        item.setDeleted(true);
        cartItemRepository.save(item);
        return cartMapper.toResponse(cart, cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId()));
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = findOrCreateCart(userId);
        cartItemRepository.findByCartIdAndIsDeletedFalse(cart.getId())
            .forEach(item -> {
                item.setDeleted(true);
                cartItemRepository.save(item);
            });
    }

    @Transactional
    private Cart findOrCreateCart(UUID userId) {
        return cartRepository.findByUserIdAndIsDeletedFalse(userId).orElseGet(() -> {
            User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }
}

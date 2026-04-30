package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.order.entity.CartItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByCartIdAndIsDeletedFalse(UUID cartId);

    Optional<CartItem> findByIdAndIsDeletedFalse(UUID id);

    Optional<CartItem> findByCartIdAndProductIdAndIsDeletedFalse(UUID cartId, UUID productId);
}

package com.fcs.be.modules.order.repository;

import com.fcs.be.modules.order.entity.Cart;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUserIdAndIsDeletedFalse(UUID userId);
}

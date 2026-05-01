package com.fcs.be.modules.wishlist.repository;

import com.fcs.be.modules.wishlist.entity.WishlistItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {

    Page<WishlistItem> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<WishlistItem> findByUserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByUserIdAndProductId(UUID userId, UUID productId);

    Optional<WishlistItem> findByUserIdAndProductId(UUID userId, UUID productId);
}

package com.fcs.be.modules.wishlist.service.interfaces;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.wishlist.dto.response.WishlistItemResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface WishlistService {

    PageResponse<WishlistItemResponse> getWishlist(UUID userId, Pageable pageable);

    void addToWishlist(UUID userId, UUID productId);

    void removeFromWishlist(UUID userId, UUID productId);
}

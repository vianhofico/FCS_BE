package com.fcs.be.modules.wishlist.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.wishlist.dto.response.WishlistItemResponse;
import com.fcs.be.modules.wishlist.service.interfaces.WishlistService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    @Test
    void testGetWishlistSuccess() throws Exception {
        WishlistItemResponse item = new WishlistItemResponse(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "SKU-1",
            "Test Product",
            new BigDecimal("100000"),
            "SELLING"
        );
        PageResponse<WishlistItemResponse> page = new PageResponse<>(List.of(item), 0, 20, 1, 1);
        when(wishlistService.getWishlist(any(UUID.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/wishlist"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].productSku").value("SKU-1"));
    }

    @Test
    void testAddToWishlistBusinessError() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new IllegalStateException("Already exists")).when(wishlistService).addToWishlist(any(UUID.class), any(UUID.class));

        mockMvc.perform(post("/api/v1/wishlist/{productId}", productId))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("BUSINESS_ERROR"));
    }

    @Test
    void testRemoveFromWishlistNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Not found")).when(wishlistService).removeFromWishlist(any(UUID.class), any(UUID.class));

        mockMvc.perform(delete("/api/v1/wishlist/{productId}", productId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }
}

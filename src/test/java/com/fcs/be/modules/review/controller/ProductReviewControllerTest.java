package com.fcs.be.modules.review.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.review.dto.response.ProductReviewResponse;
import com.fcs.be.modules.review.dto.response.ReviewSummaryResponse;
import com.fcs.be.modules.review.service.interfaces.ProductReviewService;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class ProductReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductReviewService productReviewService;

    @Test
    void testGetReviewsSuccess() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductReviewResponse review = new ProductReviewResponse(
            UUID.randomUUID(),
            productId,
            UUID.randomUUID(),
            "buyer",
            5,
            "Great",
            true,
            Instant.now()
        );
        PageResponse<ProductReviewResponse> page = new PageResponse<>(List.of(review), 0, 20, 1, 1);
        when(productReviewService.getProductReviews(any(UUID.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/products/{productId}/reviews", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].rating").value(5));
    }

    @Test
    void testGetReviewSummaryNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productReviewService.getProductReviewSummary(productId)).thenThrow(new EntityNotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/{productId}/reviews/summary", productId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void testCreateReviewValidationFail() throws Exception {
        UUID productId = UUID.randomUUID();
        String body = """
            {
              "productId": "%s",
              "rating": 6,
              "comment": "too high"
            }
            """.formatted(productId);

        mockMvc.perform(post("/api/v1/products/{productId}/reviews", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void testCreateReviewPathBodyMismatch() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID anotherId = UUID.randomUUID();
        String body = """
            {
              "productId": "%s",
              "rating": 5,
              "comment": "great"
            }
            """.formatted(anotherId);

        mockMvc.perform(post("/api/v1/products/{productId}/reviews", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("BUSINESS_ERROR"));
    }

    @Test
    void testGetReviewSummarySuccess() throws Exception {
        UUID productId = UUID.randomUUID();
        ReviewSummaryResponse summary = new ReviewSummaryResponse(4.5, 10L, 6L, 3L, 1L, 0L, 0L);
        when(productReviewService.getProductReviewSummary(productId)).thenReturn(summary);

        mockMvc.perform(get("/api/v1/products/{productId}/reviews/summary", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.averageRating").value(4.5));
    }
}

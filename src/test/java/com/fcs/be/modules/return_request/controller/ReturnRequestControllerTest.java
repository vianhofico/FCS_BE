package com.fcs.be.modules.return_request.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fcs.be.common.enums.ReturnRequestStatus;
import com.fcs.be.common.response.PageResponse;
import com.fcs.be.modules.return_request.dto.request.ReturnFilterRequest;
import com.fcs.be.modules.return_request.dto.response.ReturnRequestResponse;
import com.fcs.be.modules.return_request.service.interfaces.ReturnRequestService;
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
class ReturnRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReturnRequestService returnRequestService;

    @Test
    void testGetReturnRequestsSuccess() throws Exception {
        ReturnRequestResponse response = new ReturnRequestResponse(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Defective item",
            "http://evidence",
            ReturnRequestStatus.PENDING,
            null,
            null,
            null,
            Instant.now()
        );

        PageResponse<ReturnRequestResponse> page = new PageResponse<>(List.of(response), 0, 20, 1, 1);
        when(returnRequestService.getReturnRequests(any(ReturnFilterRequest.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/returns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].reason").value("Defective item"));
    }

    @Test
    void testCreateReturnRequestValidationFail() throws Exception {
        String body = """
            {
              "orderId": "%s",
              "reason": ""
            }
            """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/returns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    void testGetReturnRequestNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(returnRequestService.getReturnRequest(id)).thenThrow(new EntityNotFoundException("Return request not found"));

        mockMvc.perform(get("/api/v1/returns/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void testUpdateStatusBusinessError() throws Exception {
        UUID id = UUID.randomUUID();
        when(returnRequestService.updateStatus(eq(id), any(), any())).thenThrow(new IllegalStateException("Cannot change status"));

        String body = """
            {
              "status": "REFUNDED",
              "reason": "Invalid transition"
            }
            """;

        mockMvc.perform(patch("/api/v1/returns/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("BUSINESS_ERROR"));
    }
}

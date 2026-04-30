package com.fcs.be.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcs.be.common.enums.AddressType;
import com.fcs.be.modules.iam.dto.request.LoginRequest;
import com.fcs.be.modules.iam.dto.request.RegisterRequest;
import com.fcs.be.modules.iam.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ConsignmentFlowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFullConsignmentFlow() throws Exception {
        // 1. Register a new user
        RegisterRequest registerReq = new RegisterRequest("testuser", "test@test.com", "Password123!", "0901234567");
        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        String registerResponseStr = registerResult.getResponse().getContentAsString();
        // Parse the AuthResponse from ApiResponse.data
        String accessToken = objectMapper.readTree(registerResponseStr).get("data").get("accessToken").asText();

        // 2. We could do more integration tests for request creation, etc.
        // Just verifying the user can log in and out successfully for now
        LoginRequest loginReq = new LoginRequest("testuser", "Password123!");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

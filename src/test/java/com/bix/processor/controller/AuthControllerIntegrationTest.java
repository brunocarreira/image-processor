package com.bix.processor.controller;

import com.bix.processor.BaseIT;
import com.bix.processor.controller.domain.UserRequest;
import com.bix.processor.domain.SubscriptionPlan;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthControllerIntegrationTest extends BaseIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For JSON serialization

    @Test
    void testSignUp_success() throws Exception {
        // Arrange: Create a UserRequest object
        UserRequest userRequest = new UserRequest();
        userRequest.setName("testUser");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");
        userRequest.setSubscriptionPlan(SubscriptionPlan.BASIC);

        String requestBody = objectMapper.writeValueAsString(userRequest);

        // Act & Assert: Perform POST request and verify response
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("User testUser registered successfully"));
    }
}

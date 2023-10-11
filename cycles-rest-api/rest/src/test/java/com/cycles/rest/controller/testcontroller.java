package com.cycles.rest.controller;

import com.cycles.rest.dto.AddToCartRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class testcontroller {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Replace this with a valid token
    private final String authToken = "your-valid-auth-token";

    @BeforeEach
    public void setUp() {
        // Initialize any setup if needed
    }

    @Test
    @WithMockUser // Simulates an authenticated user (you can customize this annotation)
    public void testGetAllCycles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cycles/list")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Add tests for other methods in a similar manner

    @Test
    @WithMockUser // Simulates an authenticated user
    public void testAddToCart() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setId(1L);
        request.setQuantity(2);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cycles/addToCart")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

package com.tacticalcommand.tactical.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tacticalcommand.tactical.TacticalCommandHubApplication;
import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.dto.auth.LoginRequest;
import com.tacticalcommand.tactical.dto.auth.LoginResponse;

/**
 * Integration tests for Tactical Command Hub REST API.
 * 
 * This test class validates the complete application stack including
 * security, controllers, services, and database interactions using
 * an embedded test database and MockMvc for HTTP requests.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0
 * @since 2024-01-01
 */
@SpringBootTest(classes = TacticalCommandHubApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tactical Command Hub Integration Tests")
class TacticalCommandHubIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should authenticate user and return JWT token")
    void testUserAuthentication() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        // Verify response structure
        String responseContent = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseContent, LoginResponse.class);
        
        assertNotNull(loginResponse.getToken());
        assertFalse(loginResponse.getToken().isEmpty());
        assertNotNull(loginResponse.getRoles());
        assertFalse(loginResponse.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Should reject invalid credentials")
    void testInvalidAuthentication() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("invalid", "invalid");

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should create and retrieve military unit")
    void testMilitaryUnitCrud() throws Exception {
        // First authenticate to get token
        String token = authenticateAndGetToken();

        // Create a new military unit
        MilitaryUnit newUnit = new MilitaryUnit();
        newUnit.setCallsign("TEST-1");
        newUnit.setUnitName("Test Unit");
        newUnit.setUnitType(MilitaryUnit.UnitType.INFANTRY);
        newUnit.setDomain(MilitaryUnit.OperationalDomain.LAND);
        newUnit.setStatus(MilitaryUnit.UnitStatus.ACTIVE);
        newUnit.setReadinessLevel(MilitaryUnit.ReadinessLevel.C1);

        // Create unit
        MvcResult createResult = mockMvc.perform(post("/api/v1/units")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUnit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.callsign").value("TEST-1"))
                .andExpect(jsonPath("$.unitName").value("Test Unit"))
                .andReturn();

        // Extract created unit ID
        String createResponse = createResult.getResponse().getContentAsString();
        MilitaryUnit createdUnit = objectMapper.readValue(createResponse, MilitaryUnit.class);
        Long unitId = createdUnit.getId();

        // Retrieve the created unit
        mockMvc.perform(get("/api/v1/units/" + unitId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callsign").value("TEST-1"))
                .andExpect(jsonPath("$.unitName").value("Test Unit"));

        // Update unit
        createdUnit.setUnitName("Updated Test Unit");
        mockMvc.perform(put("/api/v1/units/" + unitId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdUnit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unitName").value("Updated Test Unit"));

        // Delete unit
        mockMvc.perform(delete("/api/v1/units/" + unitId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify unit is deleted
        mockMvc.perform(get("/api/v1/units/" + unitId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should require authentication for protected endpoints")
    void testSecurityProtection() throws Exception {
        // Try to access protected endpoint without token
        mockMvc.perform(get("/api/v1/units"))
                .andExpect(status().isUnauthorized());

        // Try to create unit without token
        MilitaryUnit newUnit = new MilitaryUnit();
        newUnit.setCallsign("UNAUTHORIZED-1");
        
        mockMvc.perform(post("/api/v1/units")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUnit)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return health status")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Authentication service is operational"));
    }

    @Test
    @DisplayName("Should provide API documentation")
    void testApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Helper method to authenticate and extract JWT token
     */
    private String authenticateAndGetToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseContent, LoginResponse.class);
        return loginResponse.getToken();
    }
}

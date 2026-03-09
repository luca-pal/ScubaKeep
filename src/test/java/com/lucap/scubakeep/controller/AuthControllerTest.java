package com.lucap.scubakeep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.TokenRequestDTO;
import com.lucap.scubakeep.dto.TokenResponseDTO;
import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.exception.EmailAlreadyExistsException;
import com.lucap.scubakeep.exception.UsernameAlreadyExistsException;
import com.lucap.scubakeep.service.AuthService;
import com.lucap.scubakeep.service.DiverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for {@link AuthController}.
 * Verifies public endpoints for registration and token generation.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiverService diverService;

    @MockitoBean
    private AuthService authService;

    private DiverRequestDTO diverRequestDTO;
    private DiverResponseDTO diverResponseDTO;
    private TokenRequestDTO tokenRequestDTO;

    @BeforeEach
    void setUp() {

        diverRequestDTO = new DiverRequestDTO();
        diverRequestDTO.setUsername("scubasteve");
        diverRequestDTO.setEmail("steve@example.com");
        diverRequestDTO.setPassword("Password123");
        diverRequestDTO.setFirstName("Steve");
        diverRequestDTO.setLastName("Scuba");
        diverRequestDTO.setCountryCode("FR");
        diverRequestDTO.setHighestCertification(Certification.RESCUE);
        diverRequestDTO.setSpecialties(Set.of("Deep Diver", "Nitrox"));

        diverResponseDTO = DiverResponseDTO.builder()
                .id(UUID.randomUUID())
                .username("scubasteve")
                .email("steve@example.com")
                .role(Role.USER)
                .build();

        tokenRequestDTO = new TokenRequestDTO();
        tokenRequestDTO.setIdentifier("scubasteve");
        tokenRequestDTO.setPassword("Password123");
    }

    /**
     * Tests POST /auth/register returns 201 Created and the new diver's data.
     */
    @Test
    void register_ShouldReturnCreated() throws Exception {
        when(diverService.createDiver(any(DiverRequestDTO.class))).thenReturn(diverResponseDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diverRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("scubasteve"))
                .andExpect(jsonPath("$.id").exists());
    }

    /**
     * Tests POST /auth/register returns 400 Bad Request when multiple fields fail validation.
     * <p>
     * Exercises the GlobalExceptionHandler #handleValidation logic by verifying
     * that all constraint violations (e.g., @NotBlank, @Email) are collected and
     * returned in the response map.
     */
    @Test
    void register_ShouldReturnBadRequest_WhenInputsAreInvalid() throws Exception {
        diverRequestDTO.setUsername("");        // Invalid: @NotBlank
        diverRequestDTO.setEmail("invalid-mail"); // Invalid: @Email

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diverRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    /**
     * Tests POST /auth/register returns 409 Conflict when the email is already registered.
     * <p>
     * Verifies that GlobalExceptionHandler #handleConflict correctly catches
     * {@link EmailAlreadyExistsException}.
     */
    @Test
    void register_ShouldReturnConflict_WhenEmailExists() throws Exception {
        // Arrange
        String email = "duplicate@dive.com";
        diverRequestDTO.setEmail(email);

        when(diverService.createDiver(any()))
                .thenThrow(new EmailAlreadyExistsException(email));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diverRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already in use: " + email));
    }

    /**
     * Tests POST /auth/register returns 409 Conflict when the username is already taken.
     * <p>
     * Verifies that GlobalExceptionHandler #handleConflict correctly catches
     * {@link UsernameAlreadyExistsException}.
     */
    @Test
    void register_ShouldReturnConflict_WhenUsernameExists() throws Exception {
        // Arrange
        String username = "existingDiver";
        diverRequestDTO.setUsername(username);

        when(diverService.createDiver(any()))
                .thenThrow(new UsernameAlreadyExistsException(username));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diverRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Username already in use: " + username));
    }

    /**
     * Tests POST /auth/token returns 200 OK and a JWT token.
     */
    @Test
    void token_ShouldReturnTokenResponse() throws Exception {
        TokenResponseDTO tokenResponse = new TokenResponseDTO("mocked-jwt-token");
        when(authService.authenticate(any(TokenRequestDTO.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
    }
}

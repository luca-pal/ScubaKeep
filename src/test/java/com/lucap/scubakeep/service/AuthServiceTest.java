package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.TokenRequestDTO;
import com.lucap.scubakeep.dto.TokenResponseDTO;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.exception.AuthenticatedUserNotFoundException;
import com.lucap.scubakeep.repository.DiverRepository;
import com.lucap.scubakeep.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for the {@link AuthService}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private DiverRepository diverRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private TokenRequestDTO requestDTO;
    private Diver diver;

    @BeforeEach
    void setUp() {
        requestDTO = new TokenRequestDTO();
        requestDTO.setIdentifier("testuser");
        requestDTO.setPassword("password123");

        diver = Diver.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .role(Role.USER)
                .build();
    }

    /**
     * Tests that a valid authentication request returns a valid JWT token.
     */
    @Test
    void authenticate_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(diverRepository.findByUsername("testuser")).thenReturn(Optional.of(diver));
        when(jwtService.generateToken(diver.getId(), diver.getUsername(), diver.getRole()))
                .thenReturn("mocked-jwt-token");

        // Act
        TokenResponseDTO result = authService.authenticate(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("mocked-jwt-token", result.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(diver.getId(), diver.getUsername(), diver.getRole());
    }

    /**
     * Tests that an {@link AuthenticatedUserNotFoundException} is thrown if the
     * authenticated user is not present in the database.
     */
    @Test
    void authenticate_ThrowsUserNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ghostUser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(diverRepository.findByUsername("ghostUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticatedUserNotFoundException.class, () ->
                authService.authenticate(requestDTO));

        verify(jwtService, never()).generateToken(any(), any(), any());
    }
}
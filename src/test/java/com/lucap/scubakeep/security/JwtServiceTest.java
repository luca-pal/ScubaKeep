package com.lucap.scubakeep.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lucap.scubakeep.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "test-secret-key-at-least-32-chars-long";
    private final long expiration = 3600000; // 1 hour

    private UUID userId;
    private String username;
    private Role role;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expiration);
        userId = UUID.randomUUID();
        username = "scubadiver";
        role = Role.USER;
    }

    /**
     * Tests that a token is generated with the correct claims and can be decoded.
     */
    @Test
    void generateToken_ShouldCreateValidToken() {
        // Act
        String token = jwtService.generateToken(userId, username, role);

        // Assert
        assertNotNull(token);
        DecodedJWT decoded = jwtService.decode(token);

        assertEquals(String.valueOf(userId), decoded.getSubject());
        assertEquals(username, decoded.getClaim("username").asString());
        assertEquals(role.name(), decoded.getClaim("role").asString());
    }

    /**
     * Tests that decode throws JWTVerificationException when the token is tampered with.
     */
    @Test
    void decode_ShouldThrowException_WhenTokenIsInvalid() {
        // Arrange
        String validToken = jwtService.generateToken(userId, username, role);
        String tamperedToken = validToken + "manipulated";

        // Act & Assert
        assertThrows(JWTVerificationException.class, () -> jwtService.decode(tamperedToken));
    }
}
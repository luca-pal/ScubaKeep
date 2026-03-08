package com.lucap.scubakeep.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsService.class);
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that the filter proceeds without authenticating if no Authorization header is present.
     */
    @Test
    void doFilterInternal_ShouldProceed_WhenNoHeaderPresent() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Tests that the filter proceeds without authenticating if the token format is invalid.
     */
    @Test
    void doFilterInternal_ShouldProceed_WhenHeaderIsInvalidFormat() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic credentials");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Tests successful authentication when a valid Bearer token is provided.
     */
    @Test
    void doFilterInternal_ShouldAuthenticate_WhenTokenIsValid() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        String username = "steve";
        String role = "USER";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        Claim usernameClaim = mock(Claim.class);
        Claim roleClaim = mock(Claim.class);

        when(jwtService.decode(token)).thenReturn(decodedJWT);
        when(decodedJWT.getClaim("username")).thenReturn(usernameClaim);
        when(decodedJWT.getClaim("role")).thenReturn(roleClaim);
        when(usernameClaim.asString()).thenReturn(username);
        when(roleClaim.asString()).thenReturn(role);

        UserDetails userDetails = new User(username, "", List.of());
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Tests that an invalid token does not authenticate but still proceeds with the filter chain.
     */
    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenTokenIsInvalid() throws Exception {
        String token = "invalid.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.decode(token)).thenThrow(new JWTVerificationException("Invalid"));

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}

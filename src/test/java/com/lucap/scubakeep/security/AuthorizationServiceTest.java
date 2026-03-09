package com.lucap.scubakeep.security;

import com.lucap.scubakeep.exception.UnauthorizedResourceAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    private AuthorizationService authorizationService;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService();
        authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Verifies that the check passes silently when the authenticated user's
     * username matches the resource owner's username.
     */
    @Test
    void assertOwnerOrAdmin_ShouldPass_WhenUserIsOwner() {
        // Arrange
        when(authentication.getName()).thenReturn("ownerUser");
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenAnswer(invocation -> authorities);

        // Act & Assert
        assertDoesNotThrow(() -> authorizationService.assertOwnerOrAdmin("ownerUser"));
    }

    /**
     * Verifies that the check passes silently for administrators, even if
     * they are not the resource owner.
     */
    @Test
    void assertOwnerOrAdmin_ShouldPass_WhenUserIsAdmin() {
        // Arrange
        when(authentication.getName()).thenReturn("otherUser");
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Act & Assert
        assertDoesNotThrow(() -> authorizationService.assertOwnerOrAdmin("ownerUser"));
    }

    /**
     * Verifies that an {@link UnauthorizedResourceAccessException} is thrown
     * when a non-admin user attempts to access a resource they do not own.
     */
    @Test
    void assertOwnerOrAdmin_ShouldThrow_WhenUserIsNeither() {
        // Arrange
        when(authentication.getName()).thenReturn("stranger");
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Act & Assert
        assertThrows(UnauthorizedResourceAccessException.class,
                () -> authorizationService.assertOwnerOrAdmin("ownerUser"));
    }

    /**
     * Verifies that assertAdmin passes for a user with the ADMIN role.
     */
    @Test
    void assertAdmin_ShouldPass_WhenUserIsAdmin() {
        // Arrange
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Act & Assert
        assertDoesNotThrow(() -> authorizationService.assertAdmin());
    }

    /**
     * Verifies that assertAdmin throws an {@link UnauthorizedResourceAccessException}
     * when the authenticated user does not have the ROLE_ADMIN authority.
     */
    @Test
    void assertAdmin_ShouldThrow_WhenUserIsNotAdmin() {
        // Arrange
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Act & Assert
        assertThrows(UnauthorizedResourceAccessException.class,
                () -> authorizationService.assertAdmin());
    }
}
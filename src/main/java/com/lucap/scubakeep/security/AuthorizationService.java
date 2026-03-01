package com.lucap.scubakeep.security;

import com.lucap.scubakeep.exception.UnauthorizedResourceAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Centralized authorization helper for owner/admin checks.
 * Used to enforce that only the resource owner or an admin
 * can modify or delete a resource.
 */
@Component
public class AuthorizationService {

    /**
     * Ensures that the currently authenticated user is
     * either the resource owner or an administrator.
     * <p>
     * If the user is neither the owner nor an admin, an
     * UnauthorizedResourceAccessException is thrown.
     * Returns 403 Forbidden (handled by GlobalExceptionHandler).
     */
    public void assertOwnerOrAdmin(String ownerUsername) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName(); // this is diver.getUsername()

        boolean isOwner = ownerUsername != null && ownerUsername.equals(currentUsername);

        if (!isAdmin() && !isOwner) {
            throw new UnauthorizedResourceAccessException();
        }
    }

    /**
     * Ensures that the currently authenticated user is an administrator.
     * <p>
     * If the user is not an admin, an UnauthorizedResourceAccessException is thrown.
     * Returns 403 Forbidden (handled by GlobalExceptionHandler).
     */
    public void assertAdmin() {
        if (!isAdmin()) {
            throw new UnauthorizedResourceAccessException();
        }
    }

    /**
     * Helper to checks whether the currently authenticated user has the ADMIN role.
     * <p>
     * Returns true if the user has ROLE_ADMIN, otherwise false.
     */
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

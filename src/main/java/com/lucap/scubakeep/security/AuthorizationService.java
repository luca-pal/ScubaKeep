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

    public void assertOwnerOrAdmin(String ownerUsername) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName(); // this is diver.getUsername()

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = ownerUsername != null && ownerUsername.equals(currentUsername);

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedResourceAccessException();
        }
    }
}

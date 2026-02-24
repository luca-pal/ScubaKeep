package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.TokenRequestDTO;
import com.lucap.scubakeep.dto.TokenResponseDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Authenticates users and returns an access token response.
 *
 * <p>
 * At this stage, authentication is validated via Spring Security's AuthenticationManager.
 * JWT generation will be added in the next step.
 * </p>
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public TokenResponseDTO authenticate(TokenRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentifier(),
                        request.getPassword())
        );

        // Temporary placeholder token for now.
        // Next step: generate a real JWT using the authenticated principal.
        return new TokenResponseDTO("AUTHENTICATED_OK");
    }
}
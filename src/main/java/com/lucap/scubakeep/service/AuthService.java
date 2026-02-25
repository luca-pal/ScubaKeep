package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.TokenRequestDTO;
import com.lucap.scubakeep.dto.TokenResponseDTO;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.repository.DiverRepository;
import com.lucap.scubakeep.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final DiverRepository diverRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            DiverRepository diverRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.diverRepository = diverRepository;
        this.jwtService = jwtService;
    }

    /**
     * Authenticates a user using username or email and password,
     * and returns a signed JWT if authentication succeeds.
     *
     * @param request login credentials (identifier and password)
     * @return a JWT token wrapped in TokenResponseDTO
     */
    public TokenResponseDTO authenticate(TokenRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentifier(),
                        request.getPassword())
        );

        String username = authentication.getName();

        Diver diver = diverRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found: " + username)
                );

        String token = jwtService.generateToken(
                diver.getId(),
                diver.getUsername(),
                diver.getRole()
        );
        return new TokenResponseDTO(token);
    }
}
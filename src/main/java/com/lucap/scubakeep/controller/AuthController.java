package com.lucap.scubakeep.controller;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.TokenRequestDTO;
import com.lucap.scubakeep.dto.TokenResponseDTO;
import com.lucap.scubakeep.service.AuthService;
import com.lucap.scubakeep.service.DiverService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication endpoints (registration and login).
 *
 * <p>Contains public endpoints that are required to obtain access to the system.</p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final DiverService diverService;
    private final AuthService authService;

    public AuthController(DiverService diverService, AuthService authService) {
        this.diverService = diverService;
        this.authService = authService;
    }

    /**
     * Registers a new user/diver account.
     *
     * <p>Validates input, creates the user, and returns the created user data.</p>
     *
     * @param dto registration payload
     * @return created user (201)
     */
    @PostMapping("/register")
    public ResponseEntity<DiverResponseDTO> register(@RequestBody @Valid DiverRequestDTO dto) {
        LOGGER.info("Received registration request for username='{}'", dto.getUsername());
        DiverResponseDTO created = diverService.createDiver(dto);
        LOGGER.info("User registered with ID {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Authenticates a user using username or email and password.
     *
     * @param tokenRequestDTO login request body
     * @return a token response (JWT will be returned in the future)
     */
    @PostMapping("/token")
    public TokenResponseDTO token(@RequestBody @Valid TokenRequestDTO tokenRequestDTO) {
        return authService.authenticate(tokenRequestDTO);
    }
}

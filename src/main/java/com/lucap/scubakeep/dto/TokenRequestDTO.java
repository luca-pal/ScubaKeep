package com.lucap.scubakeep.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for the login endpoint.
 * <p>
 * Contains the identifier (username or email) and password needed to authenticate
 * and generate a JWT token.
 */
@Getter
@Setter
public class TokenRequestDTO {

    @NotBlank(message = "Username or email is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}

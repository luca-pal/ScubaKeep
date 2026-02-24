package com.lucap.scubakeep.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response body returned after a successful login.
 * <p>
 * Contains the JWT token that the client must send with future requests.
 */
@Getter
@AllArgsConstructor
public class TokenResponseDTO {

    private String token;
}

package com.lucap.scubakeep.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.lucap.scubakeep.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Generates signed JWT tokens for authenticated users.
 * <p>
 * Token contains:
 * - subject: userId (UUID)
 * - claims: username, role
 * - expiration: configured via application.properties (security.jwt.expiration)
 */
@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expirationMillis;

    public JwtService(
        @Value("${security.jwt.secret}") String secret,
        @Value("${security.jwt.expiration}") long expirationMillis
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMillis = expirationMillis;
    }

    /**
     * Generates a signed JWT for the given user.
     *
     * The token contains the user's ID as subject,
     * includes username and role as claims,
     * and expires according to the configured duration.
     *
     * @param userId the UUID of the user
     * @param username the user's username
     * @param role the user's role
     * @return a signed JWT string
     */
    public String generateToken(UUID userId, String username, Role role) {

        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .withClaim("username", username)
                .withClaim("role", role.name())
                .sign(algorithm);
    }

    /**
     * Verifies the signature and expiration of the given token
     * and returns its decoded representation.
     *
     * @param token the raw JWT string
     * @return the decoded and verified JWT
     * @throws JWTVerificationException if the token is invalid or expired
     */
    public DecodedJWT decode(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}
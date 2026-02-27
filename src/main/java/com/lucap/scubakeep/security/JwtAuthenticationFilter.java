package com.lucap.scubakeep.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Spring Security filter that authenticates requests using JWT Bearer tokens.
 *
 * <p>If a valid token is present in the Authorization header, an Authentication is created
 * and stored in the SecurityContext.</p>
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    private record JwtPrincipal(String username, String role) { }

    /**
     * Extracts the JWT token from the Authorization header using the Bearer scheme.
     *
     * @param request the incoming HTTP request
     * @return an Optional containing the raw JWT token, or empty if missing/invalid format
     */
    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(authHeader.substring("Bearer ".length()));
    }

    /**
     * Builds an authenticated SecurityContext for the current request.
     *
     * @param request the current HTTP request
     * @param username the authenticated user's username
     * @param role the authenticated user's role (e.g. USER, ADMIN)
     */
    private void setAuthentication(
            HttpServletRequest request,
            String username,
            String role
    ) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Decodes and verifies the token and extracts the required authentication claims.
     *
     * @param token the raw JWT token string
     * @return a JwtPrincipal if required claims are present; otherwise {@code null}
     * @throws JWTVerificationException if the token is invalid or expired
     */
    private JwtPrincipal decodeAndConvert(String token) {
        DecodedJWT jwt = jwtService.decode(token);

        String username = jwt.getClaim("username").asString();
        String role = jwt.getClaim("role").asString();

        if (username == null || role == null) {
            return null;
        }

        return new JwtPrincipal(username, role);
    }

    /**
     * Authenticates the current request using the provided JWT token.
     *
     * @param token the raw JWT token string
     * @param request the current HTTP request
     */
    private void authenticateRequest(String token, HttpServletRequest request) {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        try {
            JwtPrincipal principal = decodeAndConvert(token);
            if (principal == null) {
                return;
            }

            setAuthentication(request, principal.username(), principal.role());

        } catch (JWTVerificationException ex) {
            // Invalid or expired token: request remains unauthenticated
        }
    }


    /**
     * Processes the request once and applies JWT authentication if a Bearer token is present.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        extractTokenFromRequest(request)
                .ifPresent(token -> authenticateRequest(token, request));

        filterChain.doFilter(request, response);
    }
}
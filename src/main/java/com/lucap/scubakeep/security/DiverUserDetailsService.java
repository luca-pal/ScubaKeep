package com.lucap.scubakeep.security;

import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.repository.DiverRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads Divers from the database for Spring Security authentication.
 */
@Service
public class DiverUserDetailsService implements UserDetailsService {

    private final DiverRepository diverRepository;

    public DiverUserDetailsService(DiverRepository diverRepository) {
        this.diverRepository = diverRepository;
    }

    /**
     * Loads a user by username (or email) for authentication.
     *
     * @param identifier username or email
     * @return UserDetails used by Spring Security
     * @throws UsernameNotFoundException if no user is found
     */
    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Diver diver = diverRepository.findByUsername(identifier)
                .or(() -> diverRepository.findByEmail(identifier))
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found (username or email): " + identifier
                        ));

        return new org.springframework.security.core.userdetails.User(
                diver.getUsername(),
                diver.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + diver.getRole().name()))
        );
    }
}
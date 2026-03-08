package com.lucap.scubakeep.security;

import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.repository.DiverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiverUserDetailsServiceTest {

    private DiverRepository diverRepository;
    private DiverUserDetailsService service;
    private Diver mockDiver;

    @BeforeEach
    void setUp() {
        diverRepository = mock(DiverRepository.class);
        service = new DiverUserDetailsService(diverRepository);

        mockDiver = new Diver();
        mockDiver.setUsername("scubasteve");
        mockDiver.setPassword("encodedPass");
        mockDiver.setRole(Role.USER);
    }

    /**
     * Tests that a valid UserDetails object is returned when a user is found by their username.
     */
    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenFoundByUsername() {
        when(diverRepository.findByUsername("scubasteve")).thenReturn(Optional.of(mockDiver));

        UserDetails result = service.loadUserByUsername("scubasteve");

        assertEquals("scubasteve", result.getUsername());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    /**
     * Tests the fallback logic: if username search returns empty, the service should
     * search by email and successfully return UserDetails.
     */
    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenFoundByEmail() {
        when(diverRepository.findByUsername("steve@dive.com")).thenReturn(Optional.empty());
        when(diverRepository.findByEmail("steve@dive.com")).thenReturn(Optional.of(mockDiver));

        UserDetails result = service.loadUserByUsername("steve@dive.com");

        assertNotNull(result);
        assertEquals("scubasteve", result.getUsername());
    }

    /**
     * Tests that a {@link UsernameNotFoundException} is thrown when neither
     * the username nor the email exists in the repository.
     */
    @Test
    void loadUserByUsername_ShouldThrowException_WhenNotFound() {
        when(diverRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(diverRepository.findByEmail("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown"));
    }
}

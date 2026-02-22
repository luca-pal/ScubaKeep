package com.lucap.scubakeep.dto;

import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object used to return diver information in API responses.
 * <p>
 * Does not include sensitive fields such as password.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiverResponseDTO {

    private UUID id;

    private String username;
    private String email;

    private String firstName;
    private String lastName;
    private String countryCode;
    private String profilePicturePath;

    private Role role;

    private int totalDives;
    private Certification highestCertification;
    private Set<String> specialties;
    private String rank;

    private Instant createdAt;
    private Instant updatedAt;
}

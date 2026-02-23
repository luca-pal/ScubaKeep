package com.lucap.scubakeep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a user (diver) in the system.
 * <p>
 * Contains authentication data (username, email, password, role),
 * personal information (name, country, profile picture),
 * and dive-related profile data such as certification level,
 * specialties, total logged dives, and a computed rank.
 * The diver can be associated with multiple dive logs.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "divers",
        // Still need to check for uniqueness at application level
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_divers_email", columnNames = "email"),
                @UniqueConstraint(name = "uq_divers_username", columnNames = "username")
        }
)
public class Diver {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", nullable = false, length = 40)
    private String username;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "country_code", length = 2, nullable = false)
    private String countryCode;

    /**
     * Optional URL or path to the user's profile picture.
     * If null, the API should respond with a default placeholder URL.
     */
    @Column(name = "profile_picture_path", length = 255)
    private String profilePicturePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 16, nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "highest_certification", nullable = false, length = 50)
    private Certification highestCertification;

    /**
     * Specialties are stored as simple String values (not as a separate entity).
     * Persisted in the "diver_specialties" table with two columns:
     * - diver_id: foreign key to divers.id
     * - specialty: the String value
     * If the set is empty, the diver has no specialties, so no rows in diver_specialties.
     */
    @ElementCollection
    @CollectionTable(
            name = "diver_specialties",
            joinColumns = @JoinColumn(name = "diver_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_diver_specialty",
                    columnNames = {"diver_id", "specialty"}
            )
    )
    @Builder.Default
    @Column(name = "specialty", nullable = false, length = 50)
    private Set<String> specialties = new HashSet<>();

    // UTC, time-zone independent compared to LocalDateTime
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    // UTC, time-zone independent compared to LocalDateTime
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

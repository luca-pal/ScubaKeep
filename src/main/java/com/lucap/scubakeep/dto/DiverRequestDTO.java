package com.lucap.scubakeep.dto;

import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.validation.CountryCode;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Data Transfer Object for creating a diver.
 * <p>
 * Validates user input from API requests and ensures proper structure before it
 * reaches the service or persistence layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiverRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 40, message = "Username must be between 5 and 40 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must be at most 120 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, and one number"
    )
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @NotBlank(message = "Country code is required")
    @CountryCode
    private String countryCode;

    /**
     * Optional profile picture URL; if null or blank, a default placeholder is used on response.
     */
    @Size(max = 255, message = "Profile picture path cannot exceed 255 characters")
    private String profilePicturePath;

    @NotNull(message = "Highest certification is required")
    private Certification highestCertification;

    @Size(max = 30, message = "Maximum 30 specialties allowed")
    private Set<@NotBlank @Size(max = 50) String> specialties;
}

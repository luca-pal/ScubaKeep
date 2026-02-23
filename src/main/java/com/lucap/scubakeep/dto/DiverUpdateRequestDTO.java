package com.lucap.scubakeep.dto;

import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.validation.CountryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Data Transfer Object for updating a diver profile.
 * <p>
 * Validates user input for profile changes before it reaches the service layer.
 * Does not include credentials or role updates.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiverUpdateRequestDTO {

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
     * Optional profile picture path; if null or blank, a default placeholder is used on response.
     */
    @Size(max = 255, message = "Profile picture path cannot exceed 255 characters")
    private String profilePicturePath;

    @NotNull(message = "Highest certification is required")
    private Certification highestCertification;

    @Size(max = 30, message = "Maximum 30 specialties allowed")
    private Set<@NotBlank @Size(max = 50) String> specialties;
}

package com.lucap.scubakeep.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Data Transfer Object for creating or updating a diver.
 * <p>
 * Validates user input from API requests and ensures proper structure before it
 * reaches the service or persistence layer. Used primarily in {@code @PostMapping}
 * and {@code @PutMapping} endpoints in the DiverController.
 */
public class DiverRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @NotNull(message = "Highest certification is required")
    private String highestCertification;

    private Set<String> specialties;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHighestCertification() {
        return highestCertification;
    }

    public void setHighestCertification(String highestCertification) {
        this.highestCertification = highestCertification;
    }

    public Set<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<String> specialties) {
        this.specialties = specialties;
    }
}

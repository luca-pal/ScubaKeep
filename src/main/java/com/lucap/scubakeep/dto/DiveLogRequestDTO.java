package com.lucap.scubakeep.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating dive log entries.
 * <p>
 * Includes validation annotations to enforce business rules before reaching the service layer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiveLogRequestDTO {

    @NotNull(message = "Dive date is required")
    private LocalDate diveDate;

    @NotBlank(message = "Location is required")
    @Size(max = 120, message = "Location must be at most 120 characters")
    private String location;

    @NotBlank(message = "Dive site is required")
    @Size(max = 120, message = "Dive site must be at most 120 characters")
    private String diveSite;

    @NotNull(message = "Maximum depth is required")
    @DecimalMin(value = "1.0", message = "Maximum depth must be 1 or greater")
    private Double maxDepth;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be 1 or greater")
    private Integer duration;

    @Size(max = 50, message = "Dive buddy must be at most 50 characters")
    private String diveBuddy;

    @Size(max = 500, message = "Notes must be at most 500 characters")
    private String notes;

    // Ownership (diver) is derived from the authenticated user (JWT),
    // not provided by the client.
    // private UUID diverId;
}

package com.lucap.scubakeep.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Data Transfer Object for updating dive log entries.
 * <p>
 * Used to validate and transfer updated dive log data from client requests.
 * Does not allow changing the owning diver.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiveLogUpdateRequestDTO {

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
    private double maxDepth;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be 1 or greater")
    private int duration;

    @Size(max = 50, message = "Dive buddy must be at most 50 characters")
    private String diveBuddy;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;
}

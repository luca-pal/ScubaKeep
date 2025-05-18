package com.lucap.scubakeep.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating dive log entries.
 * <p>
 * Used by the controller layer to validate and transfer data from client requests.
 * Includes validation annotations to enforce business rules before reaching the service layer.
 */
public class DiveLogRequestDTO {

    @NotNull(message = "Dive date is required")
    private LocalDate diveDate;

    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location must be at most 50 characters")
    private String location;

    @NotBlank(message = "Dive site is required")
    @Size(max = 50, message = "Dive site must be at most 50 characters")
    private String diveSite;

    @Min(value = 1, message = "Maximum depth must be 1 or greater")
    private double maxDepth;

    @Min(value = 1, message = "Duration must be 1 or greater")
    private int duration;

    @Size(max = 50, message = "Dive buddy must be at most 50 characters")
    private String diveBuddy;

    @Size(max = 50, message = "Notes must be at most 50 characters")
    private String notes;

    @NotNull(message = "Diver ID is required")
    private Long diverId;

    public LocalDate getDiveDate() {
        return diveDate;
    }

    public void setDiveDate(LocalDate diveDate) {
        this.diveDate = diveDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDiveSite() {
        return diveSite;
    }

    public void setDiveSite(String diveSite) {
        this.diveSite = diveSite;
    }

    public double getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(double maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDiveBuddy() {
        return diveBuddy;
    }

    public void setDiveBuddy(String diveBuddy) {
        this.diveBuddy = diveBuddy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getDiverId() {
        return diverId;
    }

    public void setDiverId(Long diverId) {
        this.diverId = diverId;
    }
}

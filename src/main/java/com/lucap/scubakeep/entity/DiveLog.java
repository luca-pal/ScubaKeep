package com.lucap.scubakeep.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Entity representing a single dive log entry.
 * <p>
 * Each record is associated with a {@link Diver} and contains metadata such as
 * date, location, site, depth, duration, and optional notes or dive buddy.
 */
@Entity
@Table(name="dive_logs")
public class DiveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Dive date is required")
    @Column(name = "dive_date", nullable = false)
    private LocalDate diveDate;

    @NotBlank(message = "Location is required")
    @Size(max = 50, message = "Location must be at most 50 characters")
    @Column(nullable = false, length = 50)
    private String location;

    @NotBlank(message = "Dive site is required")
    @Size(max = 50, message = "Dive site must be at most 50 characters")
    @Column(name = "dive_site", nullable = false, length = 50)
    private String diveSite;

    @Min(value = 1, message = "Maximum depth must be 1 or greater")
    @Column(name = "maximum_depth")
    private double maxDepth;

    @Min(value = 1, message = "Duration must be 1 or greater")
    @Column
    private int duration;

    @Size(max = 50, message = "Dive buddy must be at most 50 characters")
    @Column(name = "dive_buddy", length = 50)
    private String diveBuddy;

    @Size(max = 50, message = "Notes must be at most 50 characters")
    @Column(name = "dive_notes", length = 50)
    private String notes;

    @Valid
    @NotNull(message = "Diver must be specified")
    @ManyToOne
    @JoinColumn(name = "diver_id", nullable = false)
    private Diver diver;

    public DiveLog() {}

    public DiveLog(LocalDate diveDate, String location, String diveSite, double maxDepth,
                   int duration, String diveBuddy, String notes, Diver diver) {
        this.diveDate = diveDate;
        this.location = location;
        this.diveSite = diveSite;
        this.maxDepth = maxDepth;
        this.duration = duration;
        this.diveBuddy = diveBuddy;
        this.notes = notes;
        this.diver = diver;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Diver getDiver() {
        return diver;
    }

    public void setDiver(Diver diver) {
        this.diver = diver;
    }
}

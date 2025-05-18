package com.lucap.scubakeep.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for returning dive log information in API responses.
 * <p>
 * Includes dive metadata, such as date, location, site, depth, and duration,
 * as well as diver information for relational context.
 */
public class DiveLogResponseDTO {

    private Long id;
    private LocalDate diveDate;
    private String location;
    private String diveSite;
    private double maxDepth;
    private int duration;
    private String notes;
    private String diveBuddy;
    private Long diverId;
    private String diverName;

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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDiveBuddy() {
        return diveBuddy;
    }

    public void setDiveBuddy(String diveBuddy) {
        this.diveBuddy = diveBuddy;
    }

    public Long getDiverId() {
        return diverId;
    }

    public void setDiverId(Long diverId) {
        this.diverId = diverId;
    }

    public String getDiverName() {
        return diverName;
    }

    public void setDiverName(String diverName) {
        this.diverName = diverName;
    }
}

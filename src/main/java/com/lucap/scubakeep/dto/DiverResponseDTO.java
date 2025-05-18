package com.lucap.scubakeep.dto;

import java.util.Set;

/**
 * Data Transfer Object used to return diver information in API responses.
 * <p>
 * Contains diver identity, certification details, specialties,
 * total number of logged dives, and computed rank.
 */
public class DiverResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String highestCertification;
    private Set<String> specialties;
    private int totalDives;
    private String rank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public int getTotalDives() {
        return totalDives;
    }

    public void setTotalDives(int totalDives) {
        this.totalDives = totalDives;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}

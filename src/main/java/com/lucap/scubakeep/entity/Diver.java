package com.lucap.scubakeep.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a diver in the system.
 * <p>
 * Contains personal details, certification level, specialties,
 * total logged dives, and a computed rank based on dive count.
 * The diver can be associated with multiple dive logs.
 */
@Entity
@Table(name = "divers")
public class Diver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Min(value = 0, message = "Total dives must be 0 or greater")
    @Column(name = "total_dives")
    private int totalDives = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "highest_certification")
    private Certification highestCertification;

    @ElementCollection
    @CollectionTable(
            name = "diver_specialties",
            joinColumns = @JoinColumn(name = "diver_id"))
    @Column(name = "specialty")
    private Set<String> specialties = new HashSet<>();

    /**
     * Computed property used in JSON responses to reflect the diver's rank
     * based on the total number of dives.
     *
     * @return display name of the computed rank
     */
    @Transient
    @JsonProperty("rank")
    public String getRank() {
        return Rank.fromTotalDives(this.totalDives).getDisplayName();
    }

    public Diver() {}

    public Diver(String firstName, String lastName, int totalDives, Certification highestCertification,
                 Set<String> specialties) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalDives = totalDives;
        this.highestCertification = highestCertification;
        this.specialties = specialties;
    }

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

    public int getTotalDives() {
        return totalDives;
    }

    public void setTotalDives(int totalDives) {
        this.totalDives = totalDives;
    }

    public Certification getHighestCertification() {
        return highestCertification;
    }

    public void setHighestCertification(Certification highestCertification) {
        this.highestCertification = highestCertification;
    }

    public Set<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<String> specialties) {
        this.specialties = specialties;
    }
}

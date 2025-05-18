package com.lucap.scubakeep.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration representing diver certification levels.
 * <p>
 * Each level has a human-readable display name and is used in API responses
 * via {@link JsonValue} to serialize enum constants as strings.
 */
public enum Certification {
    UNCERTIFIED("Uncertified"),
    OPEN_WATER("Open Water Diver"),
    ADVANCED("Advanced Open Water Diver"),
    RESCUE("Rescue Diver"),
    MASTER_SCUBA("Master Scuba Diver"),
    DIVEMASTER("Divemaster"),
    INSTRUCTOR("Open Water Scuba Instructor"),
    MSD_TRAINER("Master Scuba Diver Trainer");

    private final String displayName;

    Certification(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable version of the certification.
     *
     * @return display name (like "Advanced Open Water Diver")
     */
    @JsonValue
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Converts a display name back into the corresponding Certification enum.
     *
     * @param name the display name string
     * @return the matching {@link Certification} enum constant
     * @throws IllegalArgumentException if no match is found
     */
    public static Certification fromDisplayName(String name) {
        for (Certification cert : values()) {
            if (cert.displayName.equals(name)) {
                return cert;
            }
        }
        throw new IllegalArgumentException("Invalid certification: " + name);
    }
}

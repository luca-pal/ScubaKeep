package com.lucap.scubakeep.entity;

/**
 * Enumeration representing diver certification levels.
 * <p>
 * Stored as a STRING in the database via {@code @Enumerated(EnumType.STRING)}.
 * Each level also has a human-readable display name that can be used by the UI.
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
    public String getDisplayName() {
        return this.displayName;
    }
}

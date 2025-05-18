package com.lucap.scubakeep.entity;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration representing diver ranks based on total number of logged dives.
 * <p>
 * Each rank has a human-readable label returned in API responses via {@link JsonValue}.
 * The static method {@link #fromTotalDives(int)} computes a diver's rank threshold.
 */
public enum Rank {
    NONE("Rookie Diver"),
    IRON("Iron Diver"),
    BRONZE("Bronze Diver"),
    SILVER("Silver Diver"),
    GOLD("Gold Diver"),
    PLATINUM("Platinum Diver"),
    EMERALD("Emerald Diver"),
    DIAMOND("Diamond Diver");

    private final String displayName;

    Rank(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable name of the rank.
     *
     * @return the display name (like "Gold Diver")
     */
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Determines the diver rank based on the total number of dives.
     *
     * @param totalDives the number of dives logged
     * @return the corresponding {@link Rank} enum value
     */
    public static Rank fromTotalDives(int totalDives) {
        if (totalDives >= 1000) return DIAMOND;
        else if (totalDives >= 500) return EMERALD;
        else if (totalDives >= 200) return PLATINUM;
        else if (totalDives >= 100) return GOLD;
        else if (totalDives >= 50) return SILVER;
        else if (totalDives >= 25) return BRONZE;
        else if (totalDives >= 10) return IRON;
        else return NONE;
    }
}

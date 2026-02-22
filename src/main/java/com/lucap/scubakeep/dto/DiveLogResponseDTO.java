package com.lucap.scubakeep.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Data Transfer Object for returning dive log information in API responses.
 * <p>
 * Includes dive metadata as well as diver username for relational context.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiveLogResponseDTO {

    private Long id;

    private LocalDate diveDate;
    private String location;
    private String diveSite;

    private double maxDepth;
    private int duration;

    private String notes;
    private String diveBuddy;

    private UUID diverId;
    private String diverUsername;

    private Instant createdAt;
    private Instant updatedAt;
}

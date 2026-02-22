package com.lucap.scubakeep.mapper;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;
import com.lucap.scubakeep.entity.DiveLog;
import com.lucap.scubakeep.entity.Diver;

/**
 * Mapper class responsible for converting between DiveLog entities
 * and their corresponding request and response DTOs.
 */
public class DiveLogMapper {

    /**
     * Converts a {@link DiveLogRequestDTO} and a {@link Diver} entity
     * into a new {@link DiveLog} entity.
     *
     * @param dto   the incoming request DTO containing dive log data
     * @param diver the associated Diver entity
     * @return a new DiveLog entity
     */
    public static DiveLog toEntity(DiveLogRequestDTO dto, Diver diver) {
        return DiveLog.builder()
                .diveDate(dto.getDiveDate())
                .location(dto.getLocation())
                .diveSite(dto.getDiveSite())
                .maxDepth(dto.getMaxDepth())
                .duration(dto.getDuration())
                .diveBuddy(dto.getDiveBuddy())
                .notes(dto.getNotes())
                .diver(diver)
                .build();
    }

    /**
     * Applies updates from a {@link DiveLogUpdateRequestDTO} to an existing {@link DiveLog} entity.
     * <p>
     * Does not modify the owning diver, id, or audit timestamps.
     *
     * @param diveLog the existing managed DiveLog entity
     * @param dto     the DTO containing updated dive log data
     */
    public static void applyUpdates(DiveLog diveLog, DiveLogUpdateRequestDTO dto) {
        diveLog.setDiveDate(dto.getDiveDate());
        diveLog.setLocation(dto.getLocation());
        diveLog.setDiveSite(dto.getDiveSite());
        diveLog.setMaxDepth(dto.getMaxDepth());
        diveLog.setDuration(dto.getDuration());
        diveLog.setDiveBuddy(dto.getDiveBuddy());
        diveLog.setNotes(dto.getNotes());
    }

    /**
     * Converts a {@link DiveLog} entity into a {@link DiveLogResponseDTO}.
     *
     * @param diveLog the DiveLog entity
     * @return a corresponding DiveLogResponseDTO
     */
    public static DiveLogResponseDTO toResponseDTO(DiveLog diveLog) {
        Diver diver = diveLog.getDiver();

        return DiveLogResponseDTO.builder()
                .id(diveLog.getId())
                .diveDate(diveLog.getDiveDate())
                .location(diveLog.getLocation())
                .diveSite(diveLog.getDiveSite())
                .maxDepth(diveLog.getMaxDepth())
                .duration(diveLog.getDuration())
                .notes(diveLog.getNotes())
                .diveBuddy(diveLog.getDiveBuddy())
                .diverId(diver.getId())
                .diverUsername(diver.getUsername())
                .createdAt(diveLog.getCreatedAt())
                .updatedAt(diveLog.getUpdatedAt())
                .build();
    }
}

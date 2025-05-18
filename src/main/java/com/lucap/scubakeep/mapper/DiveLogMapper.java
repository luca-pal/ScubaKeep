package com.lucap.scubakeep.mapper;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
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
        DiveLog diveLog = new DiveLog();

        diveLog.setDiveDate(dto.getDiveDate());
        diveLog.setLocation(dto.getLocation());
        diveLog.setDiveSite(dto.getDiveSite());
        diveLog.setMaxDepth(dto.getMaxDepth());
        diveLog.setDuration(dto.getDuration());
        diveLog.setDiveBuddy(dto.getDiveBuddy());
        diveLog.setNotes(dto.getNotes());
        diveLog.setDiver(diver);

        return diveLog;
    }

    /**
     * Converts a {@link DiveLog} entity into a {@link DiveLogResponseDTO}.
     *
     * @param diveLog the DiveLog entity
     * @return a corresponding DiveLogResponseDTO
     */
    public static DiveLogResponseDTO toResponseDTO(DiveLog diveLog) {
        DiveLogResponseDTO dto = new DiveLogResponseDTO();

        dto.setId(diveLog.getId());
        dto.setDiveDate(diveLog.getDiveDate());
        dto.setLocation(diveLog.getLocation());
        dto.setDiveSite(diveLog.getDiveSite());
        dto.setMaxDepth(diveLog.getMaxDepth());
        dto.setDuration(diveLog.getDuration());
        dto.setNotes(diveLog.getNotes());
        dto.setDiveBuddy(diveLog.getDiveBuddy());

        Diver diver = diveLog.getDiver();
        dto.setDiverId(diver.getId());
        dto.setDiverName(diver.getFirstName() + " " + diver.getLastName());

        return dto;
    }
}

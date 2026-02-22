package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;

import java.util.List;

/**
 * Service interface defining business operations for dive log management.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting dive logs.
 */
public interface DiveLogService {

    List<DiveLogResponseDTO> getAllDiveLogs();

    DiveLogResponseDTO createDiveLog(DiveLogRequestDTO dto);

    DiveLogResponseDTO getDiveLogById(Long id);

    void deleteDiveLog(Long id);

    DiveLogResponseDTO updateDiveLog(Long id, DiveLogUpdateRequestDTO dto);
}

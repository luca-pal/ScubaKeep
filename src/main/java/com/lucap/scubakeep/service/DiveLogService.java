package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.entity.DiveLog;

import java.util.List;

/**
 * Service interface defining business operations related to {@link DiveLog} entities.
 * <p>
 * Implementations handle creation, retrieval, update, and deletion of dive logs,
 * as well as validation and integration with other entities like {@code Diver}.
 */
public interface DiveLogService {

    /**
     * Retrieves all dive logs.
     *
     * @return list of all dive logs
     */
    List<DiveLog> getAllDiveLogs();

    /**
     * Creates a new dive log.
     *
     * @param diveLogRequestDTO the request data
     * @return the created dive log
     */
    DiveLog createDiveLog(DiveLogRequestDTO diveLogRequestDTO);

    /**
     * Retrieves a dive log by its ID.
     *
     * @param id the dive log ID
     * @return the dive log with the given ID
     */
    DiveLog getDiveLogById(Long id);

    /**
     * Deletes a dive log by its ID.
     *
     * @param id the dive log ID
     */
    void deleteDiveLog(Long id);

    /**
     * Updates an existing dive log.
     *
     * @param id  the dive log ID
     * @param dto the updated data
     * @return the updated dive log
     */
    DiveLog updateDiveLog(Long id, DiveLogRequestDTO dto);
}

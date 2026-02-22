package com.lucap.scubakeep.controller;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;
import com.lucap.scubakeep.service.DiveLogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing dive logs.
 * <p>
 * Provides endpoints for creating, retrieving, updating, and deleting dive logs.
 * All data is transferred using DTOs for request and response payloads.
 */
@RestController
@RequestMapping("/api/divelogs")
public class DiveLogController {

    private static final Logger logger = LoggerFactory.getLogger(DiveLogController.class);

    private final DiveLogService diveLogService;

    public DiveLogController(DiveLogService diveLogService) {
        this.diveLogService = diveLogService;
    }

    /**
     * Retrieves all dive logs in the system.
     *
     * @return a list of {@link DiveLogResponseDTO} representing all dive logs
     */
    @GetMapping
    public ResponseEntity<List<DiveLogResponseDTO>> getAllDiveLogs() {
        logger.info("Received request to fetch all dive logs");
        List<DiveLogResponseDTO> dtoList = diveLogService.getAllDiveLogs();
        logger.info("Returning {} dive logs", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Creates a new dive log entry.
     *
     * @param dto the details of the dive to be created
     * @return the created dive log as a {@link DiveLogResponseDTO}
     */
    @PostMapping
    public ResponseEntity<DiveLogResponseDTO> createDiveLog(@RequestBody @Valid DiveLogRequestDTO dto) {
        logger.info("Received request to create a new dive log");
        DiveLogResponseDTO created = diveLogService.createDiveLog(dto);
        logger.info("Dive log created with ID {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves a specific dive log by its ID.
     *
     * @param id the ID of the dive log to retrieve
     * @return the dive log as a {@link DiveLogResponseDTO}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiveLogResponseDTO> getDiveLogById(@PathVariable Long id) {
        logger.info("Received request to retrieve dive log with ID {}", id);
        DiveLogResponseDTO dto = diveLogService.getDiveLogById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Deletes a specific dive log by its ID.
     *
     * @param id the ID of the dive log to delete
     * @return HTTP 204 No Content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiveLogById(@PathVariable Long id) {
        logger.info("Received request to delete dive log with ID {}", id);
        diveLogService.deleteDiveLog(id);
        logger.info("Dive log with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates an existing dive log.
     *
     * @param id the ID of the dive log to update
     * @param dto the new data for the dive log
     * @return the updated dive log as a {@link DiveLogResponseDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiveLogResponseDTO> updateDiveLog(
            @PathVariable Long id,
            @RequestBody @Valid DiveLogUpdateRequestDTO dto) {
        logger.info("Received request to update dive log with ID {}", id);
        DiveLogResponseDTO updated = diveLogService.updateDiveLog(id, dto);
        logger.info("Dive log with ID {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }
}

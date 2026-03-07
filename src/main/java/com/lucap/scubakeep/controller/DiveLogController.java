package com.lucap.scubakeep.controller;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;
import com.lucap.scubakeep.service.DiveLogService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing dive logs.
 * <p>
 * Provides endpoints for creating, retrieving, updating, and deleting dive logs.
 * All data is transferred using DTOs for request and response payloads.
 */
@RestController
@RequestMapping("/api/divelogs")
public class DiveLogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiveLogController.class);

    private final DiveLogService diveLogService;

    public DiveLogController(DiveLogService diveLogService) {
        this.diveLogService = diveLogService;
    }

    /**
     * Retrieves dive logs with optional pagination and sorting.
     *
     * @param page the page index (0-based)
     * @param size the number of items per page
     * @param sortBy the field used for sorting
     * @param sortDir the sorting direction (asc or desc)
     * @param diverId optional diver id to filter dive logs by owner
     * @return a list of {@link DiveLogResponseDTO} matching the requested criteria
     */
    @GetMapping
    public ResponseEntity<List<DiveLogResponseDTO>> getAllDiveLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) UUID diverId
    ) {
        LOGGER.info(
                "Received request to fetch dive logs " +
                        "(page={}, size={}, sortBy={}, sortDir={}, diverId={})",
                page, size, sortBy, sortDir, diverId
        );

        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        List<DiveLogResponseDTO> dtoList =
                diveLogService.getDiveLogs(PageRequest.of(page, size, sort), diverId);
        LOGGER.info("Returning {} dive logs", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Creates a new dive log entry.
     *
     * @param dto the details of the dive to be created
     * @return the created dive log as a {@link DiveLogResponseDTO}
     */
    @PostMapping
    public ResponseEntity<DiveLogResponseDTO> createDiveLog(
            @RequestBody @Valid DiveLogRequestDTO dto
    ) {
        LOGGER.info("Received request to create a new dive log");
        DiveLogResponseDTO created = diveLogService.createDiveLog(dto);
        LOGGER.info("Dive log created with ID {}", created.getId());
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
        LOGGER.info("Received request to retrieve dive log with ID {}", id);
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
        LOGGER.info("Received request to delete dive log with ID {}", id);
        diveLogService.deleteDiveLog(id);
        LOGGER.info("Dive log with ID {} deleted successfully", id);
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
        LOGGER.info("Received request to update dive log with ID {}", id);
        DiveLogResponseDTO updated = diveLogService.updateDiveLog(id, dto);
        LOGGER.info("Dive log with ID {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Uploads an image for a specific dive log.
     *
     * @param id the ID of the dive log
     * @param file the image file to upload
     * @return the updated dive log as a {@link DiveLogResponseDTO}
     */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiveLogResponseDTO> uploadDiveLogImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        LOGGER.info("Received request to upload image for dive log ID {}", id);
        DiveLogResponseDTO updated = diveLogService.uploadImage(id, file);
        LOGGER.info("Image uploaded successfully for dive log ID {}", id);
        return ResponseEntity.ok(updated);
    }
}

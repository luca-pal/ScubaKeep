package com.lucap.scubakeep.controller;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.service.DiverService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing divers.
 * <p>
 * Provides endpoints to create, retrieve, update, and delete diver records.
 * Uses DTOs for request validation and response formatting.
 */
@RestController
@RequestMapping("/api/divers")
public class DiverController {

    private static final Logger logger = LoggerFactory.getLogger(DiverController.class);

    private final DiverService diverService;

    public DiverController(DiverService diverService) {
        this.diverService = diverService;
    }

    /**
     * Retrieves all divers from the system.
     *
     * @return a list of {@link DiverResponseDTO} objects
     */
    @GetMapping
    public ResponseEntity<List<DiverResponseDTO>> getAllDivers() {
        logger.info("Received request to fetch all divers");
        List<DiverResponseDTO> dtoList = diverService.getAllDivers();
        logger.info("Returning {} divers", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Creates a new diver.
     *
     * @param dto the diver data
     * @return the created diver as a {@link DiverResponseDTO}
     */
    @PostMapping
    public ResponseEntity<DiverResponseDTO> createDiver(@RequestBody @Valid DiverRequestDTO dto) {
        logger.info("Received request to create a new diver");
        DiverResponseDTO created = diverService.createDiver(dto);
        logger.info("Diver created with ID {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves a specific diver by ID.
     *
     * @param id the diver ID
     * @return the diver as a {@link DiverResponseDTO}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiverResponseDTO> getDiverById(@PathVariable UUID id) {
        logger.info("Received request to fetch diver with ID {}", id);
        DiverResponseDTO dto = diverService.getDiverById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Deletes a diver by ID.
     *
     * @param id the diver ID
     * @return HTTP 204 (No Content) if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiver(@PathVariable UUID id) {
        logger.info("Received request to delete diver with ID {}", id);
        diverService.deleteDiver(id);
        logger.info("Diver with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates a diver by ID.
     *
     * @param id the diver ID
     * @param dto the updated diver data
     * @return the updated diver as a {@link DiverResponseDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiverResponseDTO> updateDiver(
            @PathVariable UUID id,
            @RequestBody @Valid DiverUpdateRequestDTO dto) {
        logger.info("Received request to update diver with ID {}", id);
        DiverResponseDTO updated = diverService.updateDiver(id, dto);
        logger.info("Diver with ID {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }
}

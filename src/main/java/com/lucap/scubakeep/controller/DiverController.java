package com.lucap.scubakeep.controller;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.mapper.DiverMapper;
import com.lucap.scubakeep.service.DiverService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
        List<Diver> divers = diverService.getAllDivers();
        List<DiverResponseDTO> dtoList = divers.stream()
                .map(DiverMapper::toResponseDTO)
                .collect(Collectors.toList());
        logger.info("Returning {} divers", dtoList.size());
        return ResponseEntity.status(HttpStatus.OK)
                .body(dtoList);
    }

    /**
     * Creates a new diver.
     *
     * @param diverRequestDTO the diver data
     * @return the created diver as a {@link DiverResponseDTO}
     */
    @PostMapping
    public ResponseEntity<DiverResponseDTO> createDiver(@RequestBody @Valid DiverRequestDTO diverRequestDTO) {
        logger.info("Received request to create a new diver: {}", diverRequestDTO);
        Diver diver = DiverMapper.toEntity(diverRequestDTO);
        Diver savedDiver = diverService.createDiver(diver);
        DiverResponseDTO dto = DiverMapper.toResponseDTO(savedDiver);
        logger.info("Diver created with ID {}", savedDiver.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    /**
     * Retrieves a specific diver by ID.
     *
     * @param id the diver ID
     * @return the diver as a {@link DiverResponseDTO}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiverResponseDTO> getDiverById(@PathVariable Long id) {
        logger.info("Received request to fetch diver with ID {}", id);
        Diver diver = diverService.getDiverById(id);
        DiverResponseDTO dto = DiverMapper.toResponseDTO(diver);
        logger.info("Returning diver with ID {}", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    /**
     * Deletes a diver by ID.
     *
     * @param id the diver ID
     * @return HTTP 204 if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiver(@PathVariable Long id) {
        logger.info("Received request to delete diver with ID {}", id);
        diverService.deleteDiver(id);
        logger.info("Diver with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates a diver by ID.
     *
     * @param id the diver ID
     * @param diverRequestDTO the updated diver data
     * @return the updated diver as a {@link DiverResponseDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiverResponseDTO> updateDiver(@PathVariable Long id,
                                             @RequestBody @Valid DiverRequestDTO diverRequestDTO) {
        logger.info("Received request to update diver with ID {}: {}", id, diverRequestDTO);
        Diver updatedDiver = DiverMapper.toEntity(diverRequestDTO);
        Diver savedDiver = diverService.updateDiver(id, updatedDiver);
        DiverResponseDTO dto = DiverMapper.toResponseDTO(savedDiver);
        logger.info("Diver with ID {} updated successfully", id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}

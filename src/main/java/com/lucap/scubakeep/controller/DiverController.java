package com.lucap.scubakeep.controller;

import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.service.DiverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing divers.
 * <p>
 * Provides endpoints to create, retrieve, update, and delete diver records.
 * Uses DTOs for request validation and response formatting.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/divers")
public class DiverController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiverController.class);

    private final DiverService diverService;

    /**
     * Retrieves all divers from the system.
     * Just for ADMIN.
     *
     * @return a list of {@link DiverResponseDTO} objects
     */
    @GetMapping
    public ResponseEntity<List<DiverResponseDTO>> getAllDivers() {
        LOGGER.info("Received request to fetch all divers");
        List<DiverResponseDTO> dtoList = diverService.getAllDivers();
        LOGGER.info("Returning {} divers", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    // POST /api/divers removed since new users need to register through /auth/register
    // public ResponseEntity<DiverResponseDTO> createDiver(@RequestBody @Valid DiverRequestDTO dto)

    /**
     * Retrieves a specific diver by ID.
     *
     * @param id the diver ID
     * @return the diver as a {@link DiverResponseDTO}
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiverResponseDTO> getDiverById(@PathVariable UUID id) {
        LOGGER.info("Received request to fetch diver with ID {}", id);
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
        LOGGER.info("Received request to delete diver with ID {}", id);
        diverService.deleteDiver(id);
        LOGGER.info("Diver with ID {} deleted successfully", id);
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
        LOGGER.info("Received request to update diver with ID {}", id);
        DiverResponseDTO updated = diverService.updateDiver(id, dto);
        LOGGER.info("Diver with ID {} updated successfully", id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Uploads a profile picture for a specific diver.
     *
     * @param id the diver ID
     * @param file the image file to upload
     * @return the updated diver as a {@link DiverResponseDTO}
     */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiverResponseDTO> uploadProfilePicture(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        LOGGER.info("Received request to upload profile picture for diver ID {}", id);
        DiverResponseDTO updated = diverService.uploadProfilePicture(id, file);
        LOGGER.info("Profile picture uploaded successfully for diver ID {}", id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Retrieves the profile picture of a specific diver.
     *
     * @param id the diver ID
     * @return the image file as a byte array
     */
    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable UUID id) {
        LOGGER.info("Received request to download profile picture for diver ID {}", id);
        byte[] imageBytes = diverService.getProfilePictureBytes(id);

        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(imageBytes);
    }
}

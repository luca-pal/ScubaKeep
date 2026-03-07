package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;
import com.lucap.scubakeep.entity.DiveLog;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.exception.*;
import com.lucap.scubakeep.mapper.DiveLogMapper;
import com.lucap.scubakeep.repository.DiveLogRepository;
import com.lucap.scubakeep.repository.DiverRepository;
import com.lucap.scubakeep.security.AuthorizationService;
import com.lucap.scubakeep.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service implementation for dive log management.
 * <p>
 * Coordinates persistence of {@link DiveLog} entities and ensures
 * the associated {@link Diver}'s total dive count remains synchronized.
 */
@RequiredArgsConstructor
@Service
public class DiveLogServiceImpl implements DiveLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiveLogServiceImpl.class);

    private final DiveLogRepository diveLogRepository;
    private final DiverRepository diverRepository;
    private final AuthorizationService authorizationService;
    private final MinioStorageService minioStorageService;

    /**
     * Retrieves all dive logs without pagination.
     *
     * @return a list of {@link DiveLogResponseDTO} representing all dive logs
     */
    @Override
    @Transactional(readOnly = true)
    public List<DiveLogResponseDTO> getAllDiveLogs() {

        //return diveLogRepository.findAll()
        //        .stream()
        //        .map(DiveLogMapper::toResponseDTO)
        //        .toList();

        return getDiveLogs(Pageable.unpaged(), null);
    }

    /**
     * Retrieves dive logs using pagination and sorting, optionally filtered by diver.
     *
     * @param pageable the pagination and sorting configuration
     * @param diverId optional diver id to filter dive logs by owner
     * @return a list of {@link DiveLogResponseDTO} matching the requested page
     */
    @Override
    @Transactional(readOnly = true)
    public List<DiveLogResponseDTO> getDiveLogs(Pageable pageable, UUID diverId) {

        if (diverId == null) {
            LOGGER.info("Fetching dive logs (pageable={})", pageable);
            return diveLogRepository.findAll(pageable)
                    .stream()
                    .map(DiveLogMapper::toResponseDTO)
                    .toList();
        }

        LOGGER.info("Fetching dive logs for diverId={} (pageable={})", diverId, pageable);
        return diveLogRepository.findByDiverId(diverId, pageable)
                .stream()
                .map(DiveLogMapper::toResponseDTO)
                .toList();
    }

    /**
     * Creates and saves a new dive log.
     *
     * @param dto the dive log request data
     * @return the created dive log as {@link DiveLogResponseDTO}
     * @throws DiverNotFoundException if the referenced diver does not exist
     */
    @Override
    @Transactional
    public DiveLogResponseDTO createDiveLog(DiveLogRequestDTO dto) {

        // Used to extract diver id from dto
        // now it extract it from authentication, fetch managed Diver from DB
        // or throws AuthenticatedUserNotFound
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Creating dive log for authenticated user '{}'", username);

        Diver diver = diverRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticatedUserNotFoundException(username));

        DiveLog diveLog = DiveLogMapper.toEntity(dto, diver);
        DiveLog saved = diveLogRepository.save(diveLog);

        LOGGER.info("Dive log created with ID {} for diver '{}'", saved.getId(), username);
        return DiveLogMapper.toResponseDTO(saved);
    }

    /**
     * Retrieves a dive log by its ID.
     *
     * @param id the ID of the dive log
     * @return the found {@link DiveLog}
     * @throws DiveLogNotFoundException if no log is found for the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public DiveLogResponseDTO getDiveLogById(Long id) {
        LOGGER.info("Fetching dive log with ID {}", id);
        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));
        return DiveLogMapper.toResponseDTO(diveLog);
    }

    /**
     * Deletes a dive log.
     *
     * @param id the ID of the dive log to delete
     * @throws DiveLogNotFoundException if the dive log does not exist
     */
    @Override
    @Transactional
    public void deleteDiveLog(Long id) {
        LOGGER.info("Deleting dive log with ID {}", id);

        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));

        Diver diver = diveLog.getDiver();

        authorizationService.assertOwnerOrAdmin(
                diver.getUsername()
        );

        diveLogRepository.delete(diveLog);

        LOGGER.info("Dive log with ID {} deleted; diver ID {} total dives decremented",
                id,
                diver.getId()
        );
    }

    /**
     * Updates an existing dive log and updates the diver association if it has changed.
     * Handles updates to both the dive log fields and the diver’s total dive count
     * if a diver switch occurs.
     *
     * @param id  the ID of the dive log to update
     * @param dto the updated dive log data
     * @return the updated {@link DiveLog}
     * @throws DiveLogNotFoundException if no dive log is found with the given ID
     */
    @Override
    @Transactional
    public DiveLogResponseDTO updateDiveLog(Long id, DiveLogUpdateRequestDTO dto) {
        LOGGER.info("Updating dive log with ID {}", id);

        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));

        authorizationService.assertOwnerOrAdmin(
                diveLog.getDiver().getUsername()
        );

        DiveLogMapper.applyUpdates(diveLog, dto);

        LOGGER.info("Dive log with ID {} updated successfully", id);
        return DiveLogMapper.toResponseDTO(diveLog);
    }

    /**
     * Uploads an image for a specific dive log and stores it in MinIO.
     * <p>
     * Enforces authorization (only the owner or an admin can upload) and
     * validates that the uploaded file is an image.
     *
     * @param id   the ID of the dive log
     * @param file the multipart file containing the image
     * @return the updated dive log as {@link DiveLogResponseDTO}
     * @throws DiveLogNotFoundException if no dive log is found for the given ID
     * @throws InvalidFileTypeException if the uploaded file is not a valid image type
     * @throws StorageOperationException if the upload to MinIO fails
     */
    @Override
    @Transactional
    public DiveLogResponseDTO uploadImage(Long id, MultipartFile file) {
        LOGGER.info("Uploading image for dive log ID {}", id);

        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));

        authorizationService.assertOwnerOrAdmin(diveLog.getDiver().getUsername());

        // File validation
        String cType = file.getContentType();
        if (cType == null || !cType.startsWith("image/")) {
            throw new InvalidFileTypeException(cType == null ? "unknown" : cType);
        }

        // Generate a unique path for MinIO
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String objectKey = "divelogs/" + id + "/" + UUID.randomUUID() + extension;

        try {
            minioStorageService.upload(objectKey, file.getInputStream(), file.getSize(), cType);
        } catch (java.io.IOException e) {
            throw new StorageOperationException(objectKey);
        }

        diveLog.setImagePath(objectKey);
        return DiveLogMapper.toResponseDTO(diveLog);
    }

    /**
     * Retrieves the raw bytes of the dive log image from MinIO.
     *
     * @param id the ID of the dive log
     * @return the image bytes, or null if no local image exists
     */
    @Override
    @Transactional(readOnly = true)
    public byte[] getDiveLogImageBytes(Long id) {
        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));

        if (diveLog.getImagePath() == null) {
            return null;
        }

        return minioStorageService.download(diveLog.getImagePath());
    }
}

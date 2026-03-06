package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.exception.*;
import com.lucap.scubakeep.mapper.DiverMapper;
import com.lucap.scubakeep.repository.DiveLogRepository;
import com.lucap.scubakeep.repository.DiverRepository;
import com.lucap.scubakeep.security.AuthorizationService;
import com.lucap.scubakeep.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing {@link Diver} entities.
 * <p>
 * Handles business logic for CRUD operations.
 */
@RequiredArgsConstructor
@Service
public class DiverServiceImpl implements DiverService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiverServiceImpl.class);

    private final DiverRepository diverRepository;
    private final DiveLogRepository diveLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationService authorizationService;
    private final MinioStorageService minioStorageService;

    /**
     * Retrieves all divers in the system.
     *
     * @return list of all divers as {@link DiverResponseDTO}
     */
    @Override
    public List<DiverResponseDTO> getAllDivers() {
        LOGGER.info("Fetching all divers");

        authorizationService.assertAdmin();

        List<Diver> divers = diverRepository.findAll();

        // N+1 risk: This performs one COUNT query per diver.
        return divers.stream()
                .map(diver -> {
                    long totalDives = diveLogRepository.countByDiverId(diver.getId());
                    return DiverMapper.toResponseDTO(diver, totalDives);
                })
                .toList();
    }

    /**
     * Creates and persists a new diver.
     * <p>
     * Server-managed field role is applied here.
     * Password must be encoded before persistence.
     *
     * @param dto the incoming request data for creating a diver
     * @return the created diver as {@link DiverResponseDTO} with 0 totalDives
     */
    @Override
    @Transactional
    public DiverResponseDTO createDiver(DiverRequestDTO dto) {

        // 1. Check email uniqueness
        if (diverRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        // 2. Check username uniqueness
        if (diverRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        Diver diver = DiverMapper.toEntity(dto);

        // Server-managed fields
        diver.setRole(Role.USER);

        // Password handling
        diver.setPassword(passwordEncoder.encode(dto.getPassword()));

        Diver saved = diverRepository.save(diver);
        LOGGER.info("Created new diver with ID {}", saved.getId());
        return DiverMapper.toResponseDTO(saved, 0L);
    }

    /**
     * Retrieves a diver by ID.
     *
     * @param id the diver ID
     * @return the corresponding {@link DiverResponseDTO}
     * @throws DiverNotFoundException if the diver does not exist
     */
    @Override
    public DiverResponseDTO getDiverById(UUID id) {
        LOGGER.info("Fetching diver with ID {}", id);
        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));

        authorizationService.assertOwnerOrAdmin(diver.getUsername());

        long totalDives = diveLogRepository.countByDiverId(id);

        return DiverMapper.toResponseDTO(diver, totalDives);
    }

    /**
     * Deletes the diver with the given ID.
     *
     * @param id the diver ID
     * @throws DiverNotFoundException if the diver does not exist
     */
    @Override
    @Transactional
    public void deleteDiver(UUID id) {
        LOGGER.info("Deleting diver with ID {}", id);

        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));

        authorizationService.assertOwnerOrAdmin(diver.getUsername());

        diverRepository.delete(diver);
        LOGGER.info("Diver with ID {} deleted successfully", id);
    }

    /**
     * Updates a diver profile.
     *
     * @param id  the diver ID
     * @param dto the updated profile data
     * @return the updated diver as {@link DiverResponseDTO}
     * @throws DiverNotFoundException if the diver does not exist
     */
    @Override
    @Transactional
    public DiverResponseDTO updateDiver(UUID id, DiverUpdateRequestDTO dto) {
        LOGGER.info("Updating diver with ID {}", id);

        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));

        authorizationService.assertOwnerOrAdmin(diver.getUsername());

        DiverMapper.applyUpdates(diver, dto);
        long totalDives = diveLogRepository.countByDiverId(id);

        LOGGER.info("Diver with ID {} updated successfully", id);
        return DiverMapper.toResponseDTO(diver, totalDives);
    }

    /**
     * Uploads a profile picture for a specific diver and stores it in MinIO.
     * <p>
     * Enforces authorization (only the owner or an admin can update the profile picture)
     * and validates that the uploaded file is an image.
     *
     * @param id   the UUID of the diver
     * @param file the multipart file containing the image
     * @return the updated diver profile as {@link DiverResponseDTO}
     * @throws DiverNotFoundException if the diver does not exist
     * @throws InvalidFileTypeException if the uploaded file is not a valid image type
     * @throws com.lucap.scubakeep.exception.StorageOperationException if the upload to MinIO fails
     */
    @Override
    @Transactional
    public DiverResponseDTO uploadProfilePicture(UUID id, MultipartFile file) {
        LOGGER.info("Uploading profile picture for diver ID {}", id);

        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));

        authorizationService.assertOwnerOrAdmin(diver.getUsername());

        // File validation
        String cType = file.getContentType();
        if (cType == null || !cType.startsWith("image/")) {
            throw new InvalidFileTypeException(cType == null ? "unknown" : cType);
        }

        // Generate a unique path for MinIO
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String objectKey = "profiles/" + id + "/" + UUID.randomUUID() + extension;

        try {
            minioStorageService.upload(objectKey, file.getInputStream(), file.getSize(), cType);
        } catch (java.io.IOException e) {
            throw new StorageOperationException(objectKey);
        }

        diver.setProfilePicturePath(objectKey);
        long totalDives = diveLogRepository.countByDiverId(id);
        return DiverMapper.toResponseDTO(diver, totalDives);
    }
}

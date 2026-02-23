package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.exception.DiverNotFoundException;
import com.lucap.scubakeep.exception.EmailAlreadyExistsException;
import com.lucap.scubakeep.exception.UsernameAlreadyExistsException;
import com.lucap.scubakeep.mapper.DiverMapper;
import com.lucap.scubakeep.repository.DiverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for managing {@link Diver} entities.
 * <p>
 * Handles business logic for CRUD operations.
 */
@Service
public class DiverServiceImpl implements DiverService {

    private static final Logger logger = LoggerFactory.getLogger(DiverServiceImpl.class);

    private final DiverRepository diverRepository;
    // private final PasswordEncoder passwordEncoder;

    public DiverServiceImpl(DiverRepository diverRepository) {
        this.diverRepository = diverRepository;
    }

    /**
     * Retrieves all divers in the system.
     *
     * @return list of all divers as {@link DiverResponseDTO}
     */
    @Override
    public List<DiverResponseDTO> getAllDivers() {
        logger.info("Fetching all divers");
        return diverRepository.findAll()
                .stream()
                .map(DiverMapper::toResponseDTO)
                .toList();
    }

    /**
     * Creates and persists a new diver.
     * <p>
     * Server-managed field role is applied here.
     * Password must be encoded before persistence.
     *
     * @param dto the incoming request data for creating a diver
     * @return the created diver as {@link DiverResponseDTO}
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
        diver.setTotalDives(0);

        // Password handling
        // diver.setPassword(passwordEncoder.encode(dto.getPassword()));
        diver.setPassword(dto.getPassword()); // placeholder until encoder is wired

        Diver saved = diverRepository.save(diver);
        logger.info("Created new diver with ID {}", saved.getId());
        return DiverMapper.toResponseDTO(saved);
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
        logger.info("Fetching diver with ID {}", id);
        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));
        return DiverMapper.toResponseDTO(diver);
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
        logger.info("Deleting diver with ID {}", id);
        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));
        diverRepository.delete(diver);
        logger.info("Diver with ID {} deleted successfully", id);
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
        logger.info("Updating diver with ID {}", id);

        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));

        DiverMapper.applyUpdates(diver, dto);

        logger.info("Diver with ID {} updated successfully", id);
        return DiverMapper.toResponseDTO(diver);
    }
}

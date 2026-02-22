package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;
import com.lucap.scubakeep.entity.DiveLog;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.exception.DiveLogNotFoundException;
import com.lucap.scubakeep.exception.DiverNotFoundException;
import com.lucap.scubakeep.mapper.DiveLogMapper;
import com.lucap.scubakeep.repository.DiveLogRepository;
import com.lucap.scubakeep.repository.DiverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation for dive log management.
 * <p>
 * Coordinates persistence of {@link DiveLog} entities and ensures
 * the associated {@link Diver}'s total dive count remains synchronized.
 */
@Service
public class DiveLogServiceImpl implements DiveLogService {

    private static final Logger logger = LoggerFactory.getLogger(DiveLogServiceImpl.class);

    private final DiveLogRepository diveLogRepository;
    private final DiverRepository diverRepository;

    public DiveLogServiceImpl(DiveLogRepository diveLogRepository, DiverRepository diverRepository) {
        this.diveLogRepository = diveLogRepository;
        this.diverRepository = diverRepository;
    }

    /**
     * Retrieves all dive logs stored in the system.
     *
     * @return a list of all dive logs as {@link DiveLogResponseDTO}
     */
    @Override
    @Transactional(readOnly = true)
    public List<DiveLogResponseDTO> getAllDiveLogs() {
        logger.info("Fetching all dive logs");
        return diveLogRepository.findAll()
                .stream()
                .map(DiveLogMapper::toResponseDTO)
                .toList();
    }

    /**
     * Creates and saves a new dive log and increments the diver's total dive count.
     *
     * @param dto the dive log request data
     * @return the created dive log as {@link DiveLogResponseDTO}
     * @throws DiverNotFoundException if the referenced diver does not exist
     */
    @Override
    @Transactional
    public DiveLogResponseDTO createDiveLog(DiveLogRequestDTO dto) {

        // Extract diver id from dto, fetch managed Diver from DB or throws DiverNotFoundException
        UUID diverId = dto.getDiverId();
        logger.info("Creating dive log for diver ID {}", diverId);

        Diver diver = diverRepository.findById(diverId)
                .orElseThrow(() -> new DiverNotFoundException(diverId));

        DiveLog diveLog = DiveLogMapper.toEntity(dto, diver);
        DiveLog saved = diveLogRepository.save(diveLog);

        diver.setTotalDives(diver.getTotalDives() + 1);

        logger.info("Dive log created with ID {} for diver ID {}", saved.getId(), diverId);
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
        logger.info("Fetching dive log with ID {}", id);
        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));
        return DiveLogMapper.toResponseDTO(diveLog);
    }

    /**
     * Deletes a dive log and decrements the associated diver’s total dive count.
     *
     * @param id the ID of the dive log to delete
     * @throws DiveLogNotFoundException if the dive log does not exist
     */
    @Override
    @Transactional
    public void deleteDiveLog(Long id) {
        logger.info("Deleting dive log with ID {}", id);

        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));

        Diver diver = diveLog.getDiver();

        diveLogRepository.delete(diveLog);

        // decrement but not below 0
        diver.setTotalDives(Math.max(0, diver.getTotalDives() - 1));

        logger.info("Dive log with ID {} deleted; diver ID {} total dives decremented", id, diver.getId());
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
        logger.info("Updating dive log with ID {}", id);

        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));

        DiveLogMapper.applyUpdates(diveLog, dto);

        logger.info("Dive log with ID {} updated successfully", id);
        return DiveLogMapper.toResponseDTO(diveLog);
    }
}

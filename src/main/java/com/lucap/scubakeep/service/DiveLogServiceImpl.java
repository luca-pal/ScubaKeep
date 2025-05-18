package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.entity.DiveLog;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.exception.DiveLogNotFoundException;
import com.lucap.scubakeep.mapper.DiveLogMapper;
import com.lucap.scubakeep.repository.DiveLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing {@link DiveLog} entities.
 * <p>
 * Handles creation, retrieval, update, and deletion of dive logs,
 * and ensures the {@link Diver}'s total dive count stays synchronized.
 */
@Service
public class DiveLogServiceImpl implements DiveLogService {

    private static final Logger logger = LoggerFactory.getLogger(DiveLogServiceImpl.class);

    private final DiveLogRepository diveLogRepository;
    private final DiverService diverService;

    public DiveLogServiceImpl(DiveLogRepository diveLogRepository, DiverService diverService) {
        this.diveLogRepository = diveLogRepository;
        this.diverService = diverService;
    }

    /**
     * Retrieves all dive logs stored in the system.
     *
     * @return a list of all {@link DiveLog} entities
     */
    @Override
    public List<DiveLog> getAllDiveLogs() {
        logger.info("Fetching all dive logs");
        return diveLogRepository.findAll();
    }

    /**
     * Creates and saves a new dive log.
     * Also increments the diver's total dive count.
     *
     * @param dto the dive log request DTO
     * @return the saved {@link DiveLog} entity
     * @throws com.lucap.scubakeep.exception.DiverNotFoundException if the referenced diver does not exist
     */
    @Override
    @Transactional
    public DiveLog createDiveLog(DiveLogRequestDTO dto) {

        // Extract diver id from dto, fetch managed Diver from DB or throws DiverNotFoundException
        Long diverId = dto.getDiverId();
        logger.info("Creating dive log for diver ID {}", diverId);
        Diver diver = diverService.getDiverById(diverId);

        // Use mapper to convert dto + managed Diver into new DiveLog entity and persist the DiveLog
        DiveLog diveLog = DiveLogMapper.toEntity(dto, diver);
        DiveLog saved = diveLogRepository.save(diveLog);

        // Diver is a managed entity within this transaction, so change gets flushed to DB
        diverService.incrementTotalDives(diverId);
        logger.info("Dive log created with ID {} and diver {} total dives incremented", saved.getId(), diverId);
        return saved;
    }

    /**
     * Retrieves a dive log by its ID.
     *
     * @param id the ID of the dive log
     * @return the found {@link DiveLog}
     * @throws DiveLogNotFoundException if no log is found for the given ID
     */
    @Override
    public DiveLog getDiveLogById(Long id) {
        logger.info("Fetching dive log with ID {}", id);
        return diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));
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

        diveLogRepository.delete(diveLog);
        diverService.decrementTotalDives(diveLog.getDiver().getId());
        logger.info("Dive log with ID {} deleted. Diver ID {} total dives decremented",
                id, diveLog.getDiver().getId());
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
    public DiveLog updateDiveLog(Long id, DiveLogRequestDTO dto) {
        logger.info("Updating dive log with ID {}", id);
        DiveLog diveLog = diveLogRepository.findById(id)
                .orElseThrow(() -> new DiveLogNotFoundException(id));

        diveLog.setDiveDate(dto.getDiveDate());
        diveLog.setLocation(dto.getLocation());
        diveLog.setDiveSite(dto.getDiveSite());
        diveLog.setMaxDepth(dto.getMaxDepth());
        diveLog.setDuration(dto.getDuration());
        diveLog.setNotes(dto.getNotes());
        diveLog.setDiveBuddy(dto.getDiveBuddy());

        Long updatedDiverId = dto.getDiverId();
        Long oldDiverId = diveLog.getDiver().getId();

        // Check if diver changed,update totalDives if so
        if (!oldDiverId.equals(updatedDiverId)) {
            logger.info("Changing diver from ID {} to ID {}", oldDiverId, updatedDiverId);
            diverService.decrementTotalDives(oldDiverId);
            diverService.incrementTotalDives(updatedDiverId);
        }

        // Fetch the managed Diver from DB instead of using the one in the JSON
        Diver managedDiver = diverService.getDiverById(updatedDiverId);
        diveLog.setDiver(managedDiver);

        logger.info("Dive log with ID {} successfully updated", id);
        return diveLogRepository.save(diveLog);
    }
}

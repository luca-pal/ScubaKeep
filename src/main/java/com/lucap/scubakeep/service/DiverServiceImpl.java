package com.lucap.scubakeep.service;

import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.exception.DiverNotFoundException;
import com.lucap.scubakeep.repository.DiverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing {@link Diver} entities.
 * <p>
 * Handles business logic for CRUD operations and dive count updates.
 */
@Service
public class DiverServiceImpl implements DiverService {

    private static final Logger logger = LoggerFactory.getLogger(DiverServiceImpl.class);

    private final DiverRepository diverRepository;

    public DiverServiceImpl(DiverRepository diverRepository) {
        this.diverRepository = diverRepository;
    }

    /**
     * Retrieves all divers in the system.
     *
     * @return list of all {@link Diver} entities
     */
    @Override
    public List<Diver> getAllDivers() {
        logger.info("Fetching all divers");
        return diverRepository.findAll();
    }

    /**
     * Creates and persists a new diver.
     *
     * @param diver the diver to create
     * @return the saved diver entity
     */
    @Override
    @Transactional
    public Diver createDiver(Diver diver) {
        Diver saved = diverRepository.save(diver);
        logger.info("Created new diver with ID {}", saved.getId());
        return saved;
    }

    /**
     * Retrieves a diver by ID.
     *
     * @param id the diver ID
     * @return the corresponding {@link Diver}
     * @throws DiverNotFoundException if the diver does not exist
     */
    @Override
    public Diver getDiverById(Long id) {
        logger.info("Fetching diver with ID {}", id);
        return diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));
    }

    /**
     * Deletes the diver with the given ID.
     *
     * @param id the diver ID
     * @throws DiverNotFoundException if the diver does not exist
     */
    @Override
    public void deleteDiver(Long id) {
        logger.info("Deleting diver with ID {}", id);
        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));

        diverRepository.delete(diver);
        logger.info("Diver with ID {} deleted successfully", id);
    }

    /**
     * Updates the diver's personal and certification information.
     *
     * @param id           the ID of the diver to update
     * @param updatedDiver the updated diver data
     * @return the updated diver entity
     */
    @Override
    @Transactional
    public Diver updateDiver(Long id, Diver updatedDiver) {
        logger.info("Updating diver with ID {}", id);
        Diver diver = diverRepository.findById(id)
                .orElseThrow(() -> new DiverNotFoundException(id));

        diver.setFirstName(updatedDiver.getFirstName());
        diver.setLastName(updatedDiver.getLastName());
        diver.setTotalDives(updatedDiver.getTotalDives());
        diver.setHighestCertification(updatedDiver.getHighestCertification());
        diver.setSpecialties(updatedDiver.getSpecialties());

        Diver saved = diverRepository.save(diver);
        logger.info("Diver with ID {} updated successfully", id);
        return saved;
    }

    /**
     * Increments the total dives count for the given diver.
     *
     * @param diverId the diver ID
     */
    @Override
    @Transactional
    public void incrementTotalDives(Long diverId) {
        Diver diver = getDiverById(diverId);
        diver.setTotalDives(diver.getTotalDives() + 1);
        logger.info("Incremented total dives for diver ID {} to {}", diverId, diver.getTotalDives());
    }

    /**
     * Decrements the total dives count for the given diver, not below zero.
     *
     * @param diverId the diver ID
     */
    @Override
    @Transactional
    public void decrementTotalDives(Long diverId) {
        Diver diver = getDiverById(diverId);
        int newTotal = Math.max(0, diver.getTotalDives() - 1);
        diver.setTotalDives(newTotal);
        logger.info("Decremented total dives for diver ID {} to {}", diverId, newTotal);
    }
}

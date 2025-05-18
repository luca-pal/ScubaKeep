package com.lucap.scubakeep.service;

import com.lucap.scubakeep.entity.Diver;

import java.util.List;

/**
 * Service interface defining business operations related to {@link Diver} entities.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting divers,
 * as well as for incrementing and decrementing their total dive count.
 */
public interface DiverService {

    /**
     * Retrieves all divers.
     *
     * @return list of divers
     */
    List<Diver> getAllDivers();

    /**
     * Creates a new diver.
     *
     * @param diver the diver to create
     * @return the saved diver
     */
    Diver createDiver(Diver diver);

    /**
     * Retrieves a diver by ID.
     *
     * @param id the diver ID
     * @return the diver with that ID
     */
    Diver getDiverById(Long id);

    /**
     * Deletes a diver by ID.
     *
     * @param id the diver ID
     */
    void deleteDiver(Long id);

    /**
     * Updates a diver.
     *
     * @param id           the diver ID
     * @param updatedDiver the updated diver data
     * @return the updated diver
     */
    Diver updateDiver(Long id, Diver updatedDiver);

    /**
     * Increments a diver's total dives.
     *
     * @param diverId the diver ID
     */
    void incrementTotalDives(Long diverId);

    /**
     * Decrements a diver's total dives.
     *
     * @param diverId the diver ID
     */
    void decrementTotalDives(Long diverId);
}

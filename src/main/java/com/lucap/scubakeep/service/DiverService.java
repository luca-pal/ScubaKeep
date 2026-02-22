package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface defining business operations for diver management.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting divers.
 */
public interface DiverService {

    List<DiverResponseDTO> getAllDivers();

    DiverResponseDTO createDiver(DiverRequestDTO dto);

    DiverResponseDTO getDiverById(UUID id);

    void deleteDiver(UUID id);

    DiverResponseDTO updateDiver(UUID id, DiverUpdateRequestDTO dto);
}

package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface defining business operations for dive log management.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting dive logs.
 */
public interface DiveLogService {

    List<DiveLogResponseDTO> getAllDiveLogs();

    List<DiveLogResponseDTO> getDiveLogs(Pageable pageable, UUID diverId);

    DiveLogResponseDTO createDiveLog(DiveLogRequestDTO dto);

    DiveLogResponseDTO getDiveLogById(Long id);

    void deleteDiveLog(Long id);

    DiveLogResponseDTO updateDiveLog(Long id, DiveLogUpdateRequestDTO dto);

    DiveLogResponseDTO uploadImage(Long id, MultipartFile file);

    byte[] getDiveLogImageBytes(Long id);
}

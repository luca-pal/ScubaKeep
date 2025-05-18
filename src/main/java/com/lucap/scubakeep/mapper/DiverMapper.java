package com.lucap.scubakeep.mapper;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.entity.Diver;

import java.util.HashSet;

/**
 * Mapper class responsible for converting between {@link Diver} entities
 * and their corresponding request and response DTOs.
 */
public class DiverMapper {

    /**
     * Converts a {@link DiverRequestDTO} into a new {@link Diver} entity.
     * <p>
     * Initializes {@code totalDives} to 0. Converts the certification display name
     * to the corresponding {@link Certification} enum. Handles null-safe specialty set.
     *
     * @param dto the incoming DTO from client request
     * @return a new Diver entity
     */
    public static Diver toEntity(DiverRequestDTO dto) {
        Diver diver = new Diver();

        diver.setFirstName(dto.getFirstName());
        diver.setLastName(dto.getLastName());

        // Convert string to enum using exact-match method, needs control over front-end
        Certification cert = Certification.fromDisplayName(dto.getHighestCertification());
        diver.setHighestCertification(cert);

        diver.setSpecialties(dto.getSpecialties() == null
                ? new HashSet<>()
                : new HashSet<>(dto.getSpecialties()));

        diver.setTotalDives(0);

        return diver;
    }

    /**
     * Converts a {@link Diver} entity into a {@link DiverResponseDTO}.
     *
     * @param diver the Diver entity
     * @return a DTO containing diver information for API responses
     */
    public static DiverResponseDTO toResponseDTO(Diver diver) {
        DiverResponseDTO dto = new DiverResponseDTO();
        dto.setId(diver.getId());
        dto.setFirstName(diver.getFirstName());
        dto.setLastName(diver.getLastName());
        dto.setHighestCertification(diver.getHighestCertification().getDisplayName());
        dto.setSpecialties(diver.getSpecialties());
        dto.setTotalDives(diver.getTotalDives());
        dto.setRank(diver.getRank());
        return dto;
    }
}

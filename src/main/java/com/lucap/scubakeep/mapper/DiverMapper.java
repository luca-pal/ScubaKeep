package com.lucap.scubakeep.mapper;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.entity.Diver;

import java.util.HashSet;
import java.util.Set;

/**
 * Mapper class responsible for converting between {@link Diver} entities
 * and their corresponding request and response DTOs.
 */
public class DiverMapper {

    /**
     * Converts a {@link DiverRequestDTO} into a new {@link Diver} entity.
     * <p>
     * Notes:
     * <ul>
     *   <li>Does not set {@code id} or audit timestamps (managed by JPA/Hibernate).</li>
     *   <li>Does not set role (assigned by the service).</li>
     *   <li>Does not set password (service encodes and sets it before persisting).</li>
     * </ul>
     *
     * @param dto the incoming DTO from client request
     * @return a new Diver entity
     */
    public static Diver toEntity(DiverRequestDTO dto) {
        return Diver.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .countryCode(dto.getCountryCode())
                .profilePicturePath(dto.getProfilePicturePath())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .highestCertification(dto.getHighestCertification())
                .specialties(copySpecialties(dto.getSpecialties()))
                .build();
    }

    /**
     * Applies profile updates from a {@link DiverUpdateRequestDTO}
     * to an existing {@link Diver} entity.
     * <p>
     * Does not modify id, credentials, role, totalDives, or audit fields.
     *
     * @param diver the existing managed Diver entity
     * @param dto   the DTO containing updated profile data
     */
    public static void applyUpdates(Diver diver, DiverUpdateRequestDTO dto) {

        diver.setFirstName(dto.getFirstName());
        diver.setLastName(dto.getLastName());
        diver.setCountryCode(dto.getCountryCode());
        diver.setProfilePicturePath(dto.getProfilePicturePath());
        diver.setHighestCertification(dto.getHighestCertification());

        // Specialties only changes if client explicitly provides it
        if (dto.getSpecialties() != null) {
            diver.setSpecialties(copySpecialties(dto.getSpecialties()));
        }
    }

    /**
     * Converts a {@link Diver} entity into a {@link DiverResponseDTO}.
     *
     * @param diver the Diver entity
     * @return a DTO containing diver information for API responses
     */
    public static DiverResponseDTO toResponseDTO(Diver diver) {
        return DiverResponseDTO.builder()
                .id(diver.getId())
                .username(diver.getUsername())
                .email(diver.getEmail())
                .firstName(diver.getFirstName())
                .lastName(diver.getLastName())
                .countryCode(diver.getCountryCode())
                .profilePicturePath(diver.getProfilePicturePath())
                .role(diver.getRole())
                .highestCertification(diver.getHighestCertification())
                .specialties(copySpecialties(diver.getSpecialties()))
                .totalDives(diver.getTotalDives())
                .rank(diver.getRank())
                .createdAt(diver.getCreatedAt())
                .updatedAt(diver.getUpdatedAt())
                .build();
    }

    /**
     * Creates a defensive copy of the given specialties set.
     * <p>
     * Ensures that the returned set is never {@code null} and that
     * the entity does not hold a reference to the original DTO collection.
     * Returns an empty set if the input is {@code null} or empty.
     *
     * @param specialties the input set from the DTO
     * @return a non-null set safe to assign to the entity
     */
    private static Set<String> copySpecialties(Set<String> specialties) {
        if (specialties == null || specialties.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(specialties);
    }
}

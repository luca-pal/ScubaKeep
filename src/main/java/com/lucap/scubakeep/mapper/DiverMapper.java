package com.lucap.scubakeep.mapper;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.entity.Rank;

import java.util.HashSet;
import java.util.Locale;
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
                .countryCode(normalizeCountryCode(dto.getCountryCode()))
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
        diver.setCountryCode(normalizeCountryCode(dto.getCountryCode()));
        diver.setProfilePicturePath(dto.getProfilePicturePath());
        diver.setHighestCertification(dto.getHighestCertification());

        // Specialties only changes if client explicitly provides it
        if (dto.getSpecialties() != null) {
            diver.setSpecialties(copySpecialties(dto.getSpecialties()));
        }
    }

    /**
     * Converts a {@link Diver} entity into a {@link DiverResponseDTO}.
     * <p>
     * {@code totalDives} is computed from {@code DiveLog} records (not stored on the Diver entity).
     * {@code rank} is derived from the computed total dive count.
     *
     * @param diver the Diver entity
     * @param totalDives the total number of dives logged for this diver (computed externally)
     * @return a DTO containing diver information for API responses
     */
    public static DiverResponseDTO toResponseDTO(Diver diver, long totalDives) {

        long dives = Math.max(0L, totalDives);
        String rank = Rank.fromTotalDives(dives).getDisplayName();

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
                .totalDives(dives)
                .rank(rank)
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

    /**
     * Normalizes a country code string by trimming whitespace
     * and converting it to uppercase using a locale-independent rule.
     *
     * @param countryCode the raw country code input
     * @return the normalized country code in uppercase, or null if input is null
     */
    private static String normalizeCountryCode(String countryCode) {
        return countryCode == null ? null : countryCode.trim().toUpperCase(Locale.ROOT);
    }
}

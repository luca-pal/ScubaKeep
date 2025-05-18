package com.lucap.scubakeep.service;

import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.exception.DiverNotFoundException;
import com.lucap.scubakeep.repository.DiverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DiverServiceImpl.
 * <p>
 * Verifies correct behavior of diver-related business logic such as:
 * - Creating a diver
 * - Retrieving diver by ID
 * - Updating diver fields
 * - Deleting a diver
 * - Incrementing/decrementing total dives
 * - Throwing exceptions for invalid diver IDs
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class DiverServiceImplTest {

    @Mock
    private DiverRepository diverRepository;

    @InjectMocks
    private DiverServiceImpl diverService;

    private Diver buildDiver(Long id, String firstName, int totalDives, Certification cert) {
        Diver diver = new Diver();
        diver.setId(id);
        diver.setFirstName(firstName);
        diver.setLastName("The Diver");
        diver.setTotalDives(totalDives);
        diver.setHighestCertification(cert);
        diver.setSpecialties(new HashSet<>());
        return diver;
    }

    @Test
    void createDiver_shouldSaveAndReturnDiver() {

        // Fake Diver
        Diver diver = buildDiver(1L, "Dave", 0, Certification.ADVANCED);

        // Mock behavior
        when(diverRepository.save(diver)).thenReturn(diver);

        // Call real method
        Diver result = diverService.createDiver(diver);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), diver.getId());
        assertEquals(result.getFirstName(), diver.getFirstName());
        verify(diverRepository).save(any(Diver.class));
    }

    @Test
    void getDiverById_shouldReturnDiver_WhenItExists() {

        // Diver id
        Long diverId = 1L;

        // Fake Diver
        Diver diver = buildDiver(diverId, "Dave", 0, Certification.ADVANCED);

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.of(diver));

        // Call real method
        Diver result = diverService.getDiverById(diverId);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), diver.getId());
        assertEquals(result.getFirstName(), diver.getFirstName());
        verify(diverRepository).findById(diverId);
    }

    @Test
    void getDiverById_shouldThrowException_WhenDiverDoesNotExist() {

        // Not existing diver id
        Long diverId = 99L;

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.empty());

        // Call real method and assert exception
        assertThrows(DiverNotFoundException.class, () -> {
                diverService.getDiverById(diverId);
        });
    }

    @Test
    void deleteDiver_shouldRemoveDiver_WhenItExists() {

        // Diver id
        Long diverId = 1L;

        // Fake Diver
        Diver diver = buildDiver(diverId, "Dave", 0, Certification.ADVANCED);

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.of(diver));

        // Call real method
        diverService.deleteDiver(diverId);

        // Assert
        verify(diverRepository).delete(diver);
    }

    @Test
    void deleteDiver_shouldThrowException_WhenDiverDoesNotExist() {

        // Not existing diver id
        Long diverId = 99L;

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.empty());

        // Call real method and assert exception
        assertThrows(DiverNotFoundException.class, () -> {
            diverService.deleteDiver(diverId);
        });
    }

    @Test
    void updateDiver_shouldUpdateFields_WhenDiverExists() {

        // Diver id
        Long diverId = 1L;

        // Fake Diver
        Diver oldDiver = buildDiver(1L, "Dave", 0, Certification.ADVANCED);

        // New fake Diver
        Diver newDiver = buildDiver(7L, "Luca", 15, Certification.DIVEMASTER);

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.of(oldDiver));
        when(diverRepository.save(any(Diver.class))).thenReturn(oldDiver);

        // Call real method
        Diver result = diverService.updateDiver(diverId, newDiver);

        // Assert
        assertEquals(result.getId(), diverId);
        assertEquals(result.getFirstName(), newDiver.getFirstName());
        assertEquals(result.getTotalDives(), newDiver.getTotalDives());
        assertEquals(result.getHighestCertification(), newDiver.getHighestCertification());
        verify(diverRepository).save(oldDiver);
    }

    @Test
    void updateDiver_shouldThrowException_WhenDiverDoesNotExist() {

        // Not existing diver id
        Long diverId = 99L;

        // New fake Diver
        Diver newDiver = buildDiver(7L, "Luca", 15, Certification.DIVEMASTER);

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.empty());

        // Call real method and assert exception
        assertThrows(DiverNotFoundException.class, () -> {
            diverService.updateDiver(diverId, newDiver);
        });
    }

    @Test
    void incrementTotalDives_shouldIncreaseTotalByOne() {

        // Diver id
        Long diverId = 1L;

        // Fake Diver
        Diver diver = buildDiver(diverId, "Dave", 4, Certification.ADVANCED);

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.of(diver));

        // Call real method
        diverService.incrementTotalDives(diverId);

        // Assert
        assertEquals(5, diver.getTotalDives());
    }

    @Test
    void decrementTotalDives_shouldDecreaseTotalByOne_WhenTotalDivesIsPositive() {

        // Diver id
        Long diverId = 1L;

        // Fake Diver
        Diver diver = buildDiver(diverId, "Dave", 4, Certification.ADVANCED);

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.of(diver));

        // Call real method
        diverService.decrementTotalDives(diverId);

        // Assert
        assertEquals(3, diver.getTotalDives());
    }

    @Test
    void decrementTotalDives_shouldNotGoBelowZero() {

        // Diver id
        Long diverId = 1L;

        // Fake Diver
        Diver diver = buildDiver(diverId, "Dave", 0, Certification.ADVANCED);

        // Mock behavior
        when(diverRepository.findById(diverId)).thenReturn(Optional.of(diver));

        // Call real method
        diverService.decrementTotalDives(diverId);

        // Assert
        assertEquals(0, diver.getTotalDives());
    }
}

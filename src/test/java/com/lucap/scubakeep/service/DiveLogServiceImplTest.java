package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.entity.DiveLog;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.exception.DiveLogNotFoundException;
import com.lucap.scubakeep.exception.DiverNotFoundException;
import com.lucap.scubakeep.repository.DiveLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DiveLogServiceImpl}.
 * <p>
 * Verifies creation, retrieval, deletion, and update logic for dive logs,
 * including validation of diver existence and total dive tracking.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class DiveLogServiceImplTest {

    @Mock
    private DiveLogRepository diveLogRepository;

    @Mock
    private DiverService diverService;

    @InjectMocks
    private DiveLogServiceImpl diveLogService;

    private DiveLogRequestDTO buildRequest(String location, String site, int depth, int duration, Long diverId) {
        DiveLogRequestDTO dto = new DiveLogRequestDTO();
        dto.setDiveDate(LocalDate.now());
        dto.setLocation(location);
        dto.setDiveSite(site);
        dto.setMaxDepth(depth);
        dto.setDuration(duration);
        dto.setDiverId(diverId);
        return dto;
    }

    private DiveLog buildDiveLog(Long id, String location, String site, double depth, int duration, Diver diver) {
        DiveLog diveLog = new DiveLog();
        diveLog.setId(id);
        diveLog.setDiveDate(LocalDate.now());
        diveLog.setLocation(location);
        diveLog.setDiveSite(site);
        diveLog.setMaxDepth(depth);
        diveLog.setDuration(duration);
        diveLog.setDiver(diver);
        return diveLog;
    }

    @Test
    void createDiveLog_shouldSaveDiveLogAndIncrementTotalDives() {

        // Fake Diver
        Long diverId = 1L;
        Diver mockDiver = new Diver();
        mockDiver.setId(diverId);
        mockDiver.setFirstName("Test");
        mockDiver.setLastName("Diver");

        // Fake DiveLogRequestDTO
        DiveLogRequestDTO request = buildRequest("Marsa Alam", "Blue Hole", 30, 45, diverId);

        DiveLog expectedDiveLog = new DiveLog();
        expectedDiveLog.setId(123L);

        // Mock behavior
        when(diverService.getDiverById(diverId)).thenReturn(mockDiver);
        when(diveLogRepository.save(any(DiveLog.class))).thenReturn(expectedDiveLog);

        // Call real method
        DiveLog result = diveLogService.createDiveLog(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDiveLog.getId(), result.getId());
        verify(diverService).getDiverById(diverId);
        verify(diverService).incrementTotalDives(diverId);
        verify(diveLogRepository).save(any(DiveLog.class));
    }

    @Test
    void createDiveLog_shouldThrowException_WhenDiverDoesNotExist() {

        // Fake not existing Diver id
        Long diverId = 99L;

        // Fake DiveLogRequestDTO
        DiveLogRequestDTO request = buildRequest("Marsa Alam", "Blue Hole", 30, 45, diverId);

        // Mock behavior that throws exception
        when(diverService.getDiverById(diverId)).thenThrow(new DiverNotFoundException(diverId));

        // Call real method and assert exception
        assertThrows(DiverNotFoundException.class, () -> {
            diveLogService.createDiveLog(request);
        });

    }

    @Test
    void getDiveLogById_shouldReturnDiveLog_WhenItExists() {

        // input DiveLog id
        Long diveLogId = 1L;

        // Fake DiveLog in db
        DiveLog diveLog = buildDiveLog(diveLogId, "Marsa Alam", "Blue Hole",
                30, 45, new Diver());

        // Mock behaviour
        when(diveLogRepository.findById(diveLogId)).thenReturn(Optional.of(diveLog));

        // Call real method
        DiveLog result = diveLogService.getDiveLogById(diveLogId);

        // Assert
        assertNotNull(result);
        assertEquals(diveLog.getId(), result.getId());
        assertEquals(diveLog.getLocation(), result.getLocation());
        verify(diveLogRepository).findById(diveLogId);
    }

    @Test
    void getDiveLogById_shouldThrowException_WhenDiveLogDoesNotExist() {

        // input not existing DiveLog id
        Long diveLogId = 99L;

        // Mock behavior that throws exception
        when(diveLogRepository.findById(diveLogId)).thenReturn(Optional.empty());

        // Call real method and assert exception
        assertThrows(DiveLogNotFoundException.class, () -> {
            diveLogService.getDiveLogById(diveLogId);
        });
    }

    @Test
    void deleteDiveLog_shouldRemoveLogAndDecrementTotalDives_WhenItExists() {

        // Fake Diver
        Long diverId = 5L;
        Diver diver = new Diver();
        diver.setId(diverId);

        // input DiveLog id
        Long diveLogId = 1L;

        // Fake DiveLog in db
        DiveLog diveLog = buildDiveLog(diveLogId, "Marsa Alam", "Blue Hole",
                30, 45, diver);

        // Mock behaviour
        when(diveLogRepository.findById(diveLogId)).thenReturn(Optional.of(diveLog));

        // Call real method
        diveLogService.deleteDiveLog(diveLogId);

        // Verify
        verify(diveLogRepository).delete(diveLog);
        verify(diverService).decrementTotalDives(diverId);
    }

    @Test
    void deleteDiveLog_shouldThrowException_WhenDiveLogDoesNotExist() {

        // input not existing DiveLog id
        Long diveLogId = 99L;

        // Mock behavior that throws exception
        when(diveLogRepository.findById(diveLogId)).thenReturn(Optional.empty());

        // Assert
        assertThrows(DiveLogNotFoundException.class, () ->{
            diveLogService.deleteDiveLog(diveLogId);
        });
        verify(diveLogRepository, never()).delete(any());
    }

    @Test
    void updateDiveLog_shouldUpdateFields_WhenDiverStaysTheSame () {

        // input DiveLog id
        Long diveLogId = 1L;

        // Fake Diver
        Long diverId = 5L;
        Diver diver = new Diver();
        diver.setId(diverId);

        // Fake DiveLog in db
        DiveLog diveLog = buildDiveLog(diveLogId, "Marsa Alam", "Blue Hole",
                30, 45, diver);

        // Fake DiveLogRequestDTO
        DiveLogRequestDTO dto = buildRequest("Mallorca", "El Toro", 36, 41, diverId);

        // Mock behaviour
        when(diveLogRepository.findById(diveLogId)).thenReturn(Optional.of(diveLog));
        when(diverService.getDiverById(diverId)).thenReturn(diver);
        when(diveLogRepository.save(any(DiveLog.class))).thenReturn(diveLog);

        // Call real method
        DiveLog result = diveLogService.updateDiveLog(diveLogId, dto);

        // Assert
        assertEquals(result.getDiveDate(), dto.getDiveDate());
        assertEquals(result.getLocation(), dto.getLocation());
        assertEquals(result.getDiveSite(), dto.getDiveSite());
        assertEquals(result.getDiver(), diver);
    }

    @Test
    void updateDiveLog_shouldUpdateDiverAndAdjustTotalDives_WhenDiverChanges() {

        // input DiveLog id
        Long diveLogId = 1L;

        // Fake old Diver
        Long oldDiverId = 5L;
        Diver oldDiver = new Diver();
        oldDiver.setId(oldDiverId);

        // Fake new Diver
        Long newDiverId = 6L;
        Diver newDiver = new Diver();
        newDiver.setId(newDiverId);

        // Fake DiveLog in db
        DiveLog diveLog = buildDiveLog(diveLogId, "Marsa Alam", "Blue Hole",
                30, 45, oldDiver);

        // Fake DiveLogRequestDTO
        DiveLogRequestDTO dto = buildRequest("Mallorca", "El Toro", 36, 41, newDiverId);

        // Mock behaviour
        when(diveLogRepository.findById(diveLogId)).thenReturn(Optional.of(diveLog));
        when(diverService.getDiverById(newDiverId)).thenReturn(newDiver);
        when(diveLogRepository.save(any(DiveLog.class))).thenReturn(diveLog);

        // Call real method
        DiveLog result = diveLogService.updateDiveLog(diveLogId, dto);

        // Assert
        assertEquals(result.getDiveDate(), dto.getDiveDate());
        assertEquals(result.getLocation(), dto.getLocation());
        assertEquals(result.getDiveSite(), dto.getDiveSite());
        assertEquals(result.getDiver(), newDiver);
        verify(diverService).incrementTotalDives(newDiverId);
        verify(diverService).decrementTotalDives(oldDiverId);
    }
}

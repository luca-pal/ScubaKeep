package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.entity.DiveLog;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.exception.AuthenticatedUserNotFoundException;
import com.lucap.scubakeep.exception.DiveLogNotFoundException;
import com.lucap.scubakeep.repository.DiveLogRepository;
import com.lucap.scubakeep.repository.DiverRepository;
import com.lucap.scubakeep.security.AuthorizationService;
import com.lucap.scubakeep.storage.MinioStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for the {@link DiveLogServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
class DiveLogServiceImplTest {

    @Mock
    private DiveLogRepository diveLogRepository;
    @Mock
    private DiverRepository diverRepository;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private MinioStorageService minioStorageService;

    @InjectMocks
    private DiveLogServiceImpl diveLogService;

    private Diver diver;
    private DiveLog diveLog;
    private DiveLogRequestDTO requestDTO;

    /**
     * Initializes mock data objects before each test execution to ensure test isolation.
     */
    @BeforeEach
    void setUp() {
        diver = Diver.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .build();

        diveLog = DiveLog.builder()
                .id(1L)
                .diver(diver)
                .maxDepth(18.5)
                .duration(45)
                .location("Red Sea")
                .diveSite("Blue Hole")
                .build();

        requestDTO = new DiveLogRequestDTO();
        requestDTO.setDiveDate(LocalDate.now());
        requestDTO.setLocation("Malta");
        requestDTO.setDiveSite("Cirkewwa");
        requestDTO.setMaxDepth(30.0);
        requestDTO.setDuration(45);
        requestDTO.setDiveBuddy("Dave the Diver");
        requestDTO.setNotes("Saw a tuna, now I crave sushi.");
    }

    /**
     * Tests that requesting all dive logs correctly passes an unpaged
     * request with a null ID to the underlying method.
     */
    @Test
    void getAllDiveLogs_Success() {
        // Arrange
        PageImpl<DiveLog> diveLogPage = new PageImpl<>(List.of(diveLog));
        when(diveLogRepository.findAll(any(Pageable.class))).thenReturn(diveLogPage);

        // Act
        List<DiveLogResponseDTO> result = diveLogService.getAllDiveLogs();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(diveLogRepository, times(1)).findAll(Pageable.unpaged());
    }

    /**
     * Tests that fetching dive logs with pagination but without a specific
     * diver ID correctly calls findAll() on the repository.
     */
    @Test
    void getDiveLogs_WithoutDiverId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<DiveLog> diveLogPage = new PageImpl<>(List.of(diveLog));
        when(diveLogRepository.findAll(eq(pageable))).thenReturn(diveLogPage);

        // Act
        List<DiveLogResponseDTO> result = diveLogService.getDiveLogs(pageable, null);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(diveLogRepository, times(1)).findAll(pageable);
        verify(diveLogRepository, never()).findByDiverId(any(), any());
    }

    /**
     * Tests that fetching dive logs for a specific diver correctly routes
     * the call to findByDiverId() on the repository.
     */
    @Test
    void getDiveLogs_WithDiverId_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<DiveLog> diveLogPage = new PageImpl<>(List.of(diveLog));
        when(diveLogRepository.findByDiverId(eq(diver.getId()), eq(pageable))).thenReturn(diveLogPage);

        // Act
        List<DiveLogResponseDTO> result = diveLogService.getDiveLogs(pageable, diver.getId());

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(diveLogRepository, times(1)).findByDiverId(diver.getId(), pageable);
        verify(diveLogRepository, never()).findAll(any(Pageable.class));
    }

    /**
     * Tests that a dive log is successfully created for the currently
     * authenticated user.
     */
    @Test
    void createDiveLog_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(diverRepository.findByUsername("testuser")).thenReturn(Optional.of(diver));
        when(diveLogRepository.save(any(DiveLog.class))).thenReturn(diveLog);

        // Act
        DiveLogResponseDTO result = diveLogService.createDiveLog(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(diveLog.getLocation(), result.getLocation());
        verify(diveLogRepository, times(1)).save(any(DiveLog.class));

        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that if the authenticated username is not found in the database,
     * an {@link AuthenticatedUserNotFoundException} is thrown.
     */
    @Test
    void createDiveLog_ThrowsAuthenticatedUserNotFound() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.getName()).thenReturn("unknownUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(diverRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticatedUserNotFoundException.class, () ->
                diveLogService.createDiveLog(requestDTO));

        verify(diveLogRepository, never()).save(any(DiveLog.class));

        SecurityContextHolder.clearContext();
    }

    /**
     * Tests that a dive log is successfully retrieved when a valid ID is provided.
     */
    @Test
    void getDiveLogById_Success() {
        // Arrange
        when(diveLogRepository.findById(1L)).thenReturn(Optional.of(diveLog));

        // Act
        DiveLogResponseDTO result = diveLogService.getDiveLogById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Red Sea", result.getLocation());
        verify(diveLogRepository, times(1)).findById(1L);
    }

    /**
     * Tests that a {@link DiveLogNotFoundException} is thrown when
     * searching for a non-existent dive log ID.
     */
    @Test
    void getDiveLogById_ThrowsNotFound() {
        // Arrange
        when(diveLogRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DiveLogNotFoundException.class, () ->
                diveLogService.getDiveLogById(999L));

        verify(diveLogRepository, times(1)).findById(999L);
    }

    /**
     * Tests that a dive log is successfully deleted when the log exists
     * and the user is authorized (owner or admin).
     */
    @Test
    void deleteDiveLog_Success() {
        // Arrange
        Long logId = 1L;
        when(diveLogRepository.findById(logId)).thenReturn(Optional.of(diveLog));
        doNothing().when(authorizationService).assertOwnerOrAdmin(diver.getUsername());

        // Act
        diveLogService.deleteDiveLog(logId);

        // Assert
        verify(authorizationService, times(1)).assertOwnerOrAdmin(diver.getUsername());
        verify(diveLogRepository, times(1)).delete(diveLog);
    }

    /**
     * Tests that attempting to delete a non-existent dive log
     * throws a {@link DiveLogNotFoundException}.
     */
    @Test
    void deleteDiveLog_ThrowsNotFound() {
        // Arrange
        Long logId = 999L;
        when(diveLogRepository.findById(logId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DiveLogNotFoundException.class, () ->
                diveLogService.deleteDiveLog(logId));

        verify(diveLogRepository, never()).delete(any());
    }
}
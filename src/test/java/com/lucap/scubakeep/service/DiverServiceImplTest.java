package com.lucap.scubakeep.service;

import com.lucap.scubakeep.dto.DiverRequestDTO;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.entity.Diver;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.exception.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit test suite for the {@link DiverServiceImpl} class.
 */
@ExtendWith(MockitoExtension.class)
class DiverServiceImplTest {

    @Mock
    private DiverRepository diverRepository;
    @Mock
    private DiveLogRepository diveLogRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthorizationService authorizationService;
    @Mock
    private MinioStorageService minioStorageService;

    @InjectMocks
    private DiverServiceImpl diverService;

    private DiverRequestDTO requestDTO;
    private DiverUpdateRequestDTO updateRequestDTO;
    private Diver diver;

    /**
     * Initializes mock data objects before each test execution to ensure test isolation.
     */
    @BeforeEach
    void setUp() {
        requestDTO = new DiverRequestDTO();
        requestDTO.setUsername("testuser");
        requestDTO.setEmail("test@test.com");
        requestDTO.setPassword("Password123!");
        requestDTO.setFirstName("Test");
        requestDTO.setLastName("User");
        requestDTO.setCountryCode("AT");
        requestDTO.setHighestCertification(Certification.OPEN_WATER);

        updateRequestDTO = new DiverUpdateRequestDTO();
        updateRequestDTO.setFirstName("UpdatedFirstName");
        updateRequestDTO.setLastName("UpdatedLastName");
        updateRequestDTO.setCountryCode("IT");
        updateRequestDTO.setProfilePicturePath("https://robohash.org/scuba");
        updateRequestDTO.setHighestCertification(Certification.ADVANCED);
        updateRequestDTO.setSpecialties(Set.of("Wreck Diver", "Deep Diver"));

        diver = Diver.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@test.com")
                .role(Role.USER)
                .firstName("Test")
                .lastName("User")
                .build();
    }

    /**
     * Tests that an administrator can successfully retrieve a list of all registered divers.
     */
    @Test
    void getAllDivers_Success() {
        // Arrange
        doNothing().when(authorizationService).assertAdmin();
        when(diverRepository.findAll()).thenReturn(List.of(diver));
        when(diveLogRepository.countByDiverId(any(UUID.class))).thenReturn(10L);

        // Act
        List<DiverResponseDTO> result = diverService.getAllDivers();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getTotalDives());
        verify(authorizationService, times(1)).assertAdmin();
    }

    /**
     * Tests that a new diver is successfully created and saved to the database
     * when all provided data is valid and unique.
     */
    @Test
    void createDiver_Success() {
        // Arrange
        when(diverRepository.existsByEmail(anyString())).thenReturn(false);
        when(diverRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(diverRepository.save(any(Diver.class))).thenReturn(diver);

        // Act
        DiverResponseDTO response = diverService.createDiver(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(diverRepository, times(1)).save(any(Diver.class));
    }

    /**
     * Tests that a registration attempt fails and throws an {@link EmailAlreadyExistsException}
     * if the requested email address is already in use.
     */
    @Test
    void createDiver_ThrowsEmailAlreadyExistsException() {
        // Arrange
        when(diverRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> diverService.createDiver(requestDTO));
        verify(diverRepository, never()).save(any(Diver.class));
    }

    /**
     * Tests that a registration attempt fails and throws a {@link UsernameAlreadyExistsException}
     * if the requested username is already in use.
     */
    @Test
    void createDiver_ThrowsUsernameAlreadyExistsException() {
        // Arrange
        when(diverRepository.existsByEmail(anyString())).thenReturn(false);
        when(diverRepository.existsByUsername(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> diverService.createDiver(requestDTO));
        verify(diverRepository, never()).save(any(Diver.class));
    }

    /**
     * Tests that a specific diver can be retrieved by their unique ID, assuming the
     * requester has the correct authorization.
     */
    @Test
    void getDiverById_Success() {
        // Arrange
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.of(diver));
        doNothing().when(authorizationService).assertOwnerOrAdmin(anyString());
        when(diveLogRepository.countByDiverId(any(UUID.class))).thenReturn(5L);

        // Act
        DiverResponseDTO result = diverService.getDiverById(diver.getId());

        // Assert
        assertNotNull(result);
        assertEquals(diver.getUsername(), result.getUsername());
        assertEquals(5L, result.getTotalDives());
        verify(authorizationService, times(1)).assertOwnerOrAdmin(diver.getUsername());
    }

    /**
     * Tests that attempting to retrieve a non-existent diver throws a {@link DiverNotFoundException}.
     */
    @Test
    void getDiverById_ThrowsNotFound() {
        // Arrange
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DiverNotFoundException.class, () -> diverService.getDiverById(UUID.randomUUID()));
    }

    /**
     * Tests that a diver record can be successfully deleted from the system,
     * assuming the requester has the correct authorization.
     */
    @Test
    void deleteDiver_Success() {
        // Arrange
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.of(diver));
        doNothing().when(authorizationService).assertOwnerOrAdmin(anyString());
        doNothing().when(diverRepository).delete(any(Diver.class));

        // Act
        diverService.deleteDiver(diver.getId());

        // Assert
        verify(diverRepository, times(1)).delete(diver);
        verify(authorizationService, times(1)).assertOwnerOrAdmin(diver.getUsername());
    }

    /**
     * Tests that an existing diver's profile is successfully updated.
     * Verifies that authorization is checked, the entity fields are modified
     * (relying on @Transactional for saving), and the data is correctly mapped.
     */
    @Test
    void updateDiver_Success() {
        // Arrange
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.of(diver));
        doNothing().when(authorizationService).assertOwnerOrAdmin(anyString());
        when(diveLogRepository.countByDiverId(any(UUID.class))).thenReturn(5L);

        // Act
        DiverResponseDTO result = diverService.updateDiver(diver.getId(), updateRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getTotalDives());
        verify(authorizationService, times(1)).assertOwnerOrAdmin(diver.getUsername());

        // Assert that the diver entity was successfully modified by the service
        assertEquals("UpdatedFirstName", diver.getFirstName());
        assertEquals("UpdatedLastName", diver.getLastName());
        assertEquals("IT", diver.getCountryCode());
        assertEquals("https://robohash.org/scuba", diver.getProfilePicturePath());
    }

    /**
     * Tests that attempting to update a non-existent diver throws
     * a {@link DiverNotFoundException} and does not attempt to save any data.
     */
    @Test
    void updateDiver_ThrowsNotFound() {
        // Arrange
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DiverNotFoundException.class, () ->
                diverService.updateDiver(UUID.randomUUID(), updateRequestDTO));
        verify(diverRepository, never()).save(any(Diver.class));
    }

    /**
     * Tests that a user can successfully upload and save a new profile picture.
     * Verifies that security is checked, the file stream is passed to MinIO,
     * and the resulting path is saved to the diver entity.
     */
    @Test
    void uploadProfilePicture_Success() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        InputStream mockStream = mock(InputStream.class);

        // Mock the MultipartFile behavior to match MinioStorageService requirements
        when(mockFile.getInputStream()).thenReturn(mockStream);
        when(mockFile.getSize()).thenReturn(2048L);
        when(mockFile.getContentType()).thenReturn("image/png");

        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.of(diver));
        doNothing().when(authorizationService).assertOwnerOrAdmin(anyString());

        // Mock the void upload method
        doNothing().when(minioStorageService).upload(
                anyString(), eq(mockStream), eq(2048L), eq("image/png")
        );

        // Act
        diverService.uploadProfilePicture(diver.getId(), mockFile);

        // Assert
        assertNotNull(diver.getProfilePicturePath());
        verify(authorizationService, times(1)).assertOwnerOrAdmin(diver.getUsername());
        verify(minioStorageService, times(1)).upload(
                anyString(), eq(mockStream), eq(2048L), eq("image/png")
        );
    }

    /**
     * Tests that attempting to upload a picture for a non-existent diver
     * throws a {@link DiverNotFoundException} and prevents the MinIO upload.
     */
    @Test
    void uploadProfilePicture_ThrowsNotFound() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DiverNotFoundException.class, () ->
                diverService.uploadProfilePicture(UUID.randomUUID(), mockFile));

        // Ensure we don't accidentally upload orphan files to MinIO!
        verify(minioStorageService, never()).upload(anyString(), any(), anyLong(), anyString());
    }

    /**
     * Tests that uploading a file with a non-image content type
     * throws an {@link InvalidFileTypeException}.
     */
    @Test
    void uploadProfilePicture_ThrowsInvalidFileTypeException() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.of(diver));
        doNothing().when(authorizationService).assertOwnerOrAdmin(anyString());

        // Trigger the if-statement by returning a non-image content type
        when(mockFile.getContentType()).thenReturn("application/pdf");

        // Act & Assert
        assertThrows(InvalidFileTypeException.class, () ->
                diverService.uploadProfilePicture(diver.getId(), mockFile));
        verify(minioStorageService, never()).upload(anyString(), any(), anyLong(), anyString());
    }

    /**
     * Tests that if reading the file stream fails, the service catches
     * the IOException and throws a {@link StorageOperationException}.
     */
    @Test
    void uploadProfilePicture_ThrowsStorageOperationException() throws Exception {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.of(diver));
        doNothing().when(authorizationService).assertOwnerOrAdmin(anyString());
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getOriginalFilename()).thenReturn("photo.jpg");

        // Trigger the catch block by forcing the input stream to throw an IOException
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated stream error"));

        // Act & Assert
        assertThrows(StorageOperationException.class, () ->
                diverService.uploadProfilePicture(diver.getId(), mockFile));
        assertNull(diver.getProfilePicturePath());
    }

    /**
     * Tests that a valid MinIO object path correctly fetches and returns
     * the raw byte array from the storage service.
     */
    @Test
    void getProfilePictureBytes_Success() {
        // Arrange
        String validPath = "profiles/" + diver.getId() + "/image.jpg";
        diver.setProfilePicturePath(validPath);
        byte[] expectedBytes = new byte[]{1, 2, 3, 4, 5};

        when(diverRepository.findById(diver.getId())).thenReturn(Optional.of(diver));
        when(minioStorageService.download(validPath)).thenReturn(expectedBytes);

        // Act
        byte[] actualBytes = diverService.getProfilePictureBytes(diver.getId());

        // Assert
        assertNotNull(actualBytes);
        assertArrayEquals(expectedBytes, actualBytes);
        verify(minioStorageService, times(1)).download(validPath);
    }

    /**
     * Tests that if a diver does not have a profile picture set,
     * the method safely returns null without calling MinIO.
     */
    @Test
    void getProfilePictureBytes_ReturnsNullWhenPathIsNull() {
        // Arrange
        diver.setProfilePicturePath(null);
        when(diverRepository.findById(diver.getId())).thenReturn(Optional.of(diver));

        // Act
        byte[] actualBytes = diverService.getProfilePictureBytes(diver.getId());

        // Assert
        assertNull(actualBytes);
        verify(minioStorageService, never()).download(anyString());
    }

    /**
     * Tests that if a diver's profile picture is an external web link,
     * the method safely returns null without calling MinIO.
     */
    @Test
    void getProfilePictureBytes_ReturnsNullWhenPathIsHttp() {
        // Arrange
        diver.setProfilePicturePath("https://robohash.org/scuba");
        when(diverRepository.findById(diver.getId())).thenReturn(Optional.of(diver));

        // Act
        byte[] actualBytes = diverService.getProfilePictureBytes(diver.getId());

        // Assert
        assertNull(actualBytes);
        verify(minioStorageService, never()).download(anyString());
    }

    /**
     * Tests that requesting bytes for a non-existent diver
     * throws a {@link DiverNotFoundException}.
     */
    @Test
    void getProfilePictureBytes_ThrowsNotFound() {
        // Arrange
        when(diverRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DiverNotFoundException.class, () ->
                diverService.getProfilePictureBytes(UUID.randomUUID()));
        verify(minioStorageService, never()).download(anyString());
    }
}

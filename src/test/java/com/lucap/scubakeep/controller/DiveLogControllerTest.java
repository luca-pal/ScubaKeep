package com.lucap.scubakeep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucap.scubakeep.dto.DiveLogRequestDTO;
import com.lucap.scubakeep.dto.DiveLogResponseDTO;
import com.lucap.scubakeep.dto.DiveLogUpdateRequestDTO;
import com.lucap.scubakeep.exception.DiveLogNotFoundException;
import com.lucap.scubakeep.exception.UnauthorizedResourceAccessException;
import com.lucap.scubakeep.service.DiveLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiveLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiveLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DiveLogService diveLogService;

    private DiveLogResponseDTO responseDTO;
    private DiveLogRequestDTO requestDTO;
    private DiveLogUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = DiveLogResponseDTO.builder()
                .id(1L)
                .location("Blue Hole")
                .diveSite("The Arch")
                .diveDate(LocalDate.now())
                .maxDepth(30.0)
                .duration(45)
                .diverId(UUID.randomUUID())
                .build();

        requestDTO = new DiveLogRequestDTO();
        requestDTO.setLocation("Red Sea");
        requestDTO.setDiveSite("Shark Reef");
        requestDTO.setDiveDate(LocalDate.now().minusDays(1));
        requestDTO.setMaxDepth(20.0);
        requestDTO.setDuration(50);
        requestDTO.setDiveBuddy("Scuba Sam");
        requestDTO.setNotes("Initial notes");

        updateRequestDTO = new DiveLogUpdateRequestDTO();
        updateRequestDTO.setLocation("Updated Location");
        updateRequestDTO.setDiveSite("Updated Site");
        updateRequestDTO.setDiveDate(LocalDate.now());
        updateRequestDTO.setMaxDepth(35.0);
        updateRequestDTO.setDuration(55);
        updateRequestDTO.setDiveBuddy("Elena");
        updateRequestDTO.setNotes("Updated notes");
    }

    /**
     * Tests GET /api/divelogs with default pagination and sorting.
     * Also exercises the "desc" branch of the sort logic.
     */
    @Test
    void getAllDiveLogs_ShouldReturnPagedList() throws Exception {
        when(diveLogService.getDiveLogs(any(Pageable.class), any())).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/divelogs")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Blue Hole"));
    }

    /**
     * Tests GET /api/divelogs with "asc" to exercise the other branch of sort logic.
     */
    @Test
    void getAllDiveLogs_Ascending_ShouldReturnList() throws Exception {
        when(diveLogService.getDiveLogs(any(Pageable.class), any())).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/divelogs")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());
    }

    /**
     * Tests POST /api/divelogs returns 201 Created.
     */
    @Test
    void createDiveLog_ShouldReturnCreated() throws Exception {
        // Arrange
        when(diveLogService.createDiveLog(any(DiveLogRequestDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/divelogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.location").value("Blue Hole"));
    }

    /**
     * Tests GET /api/divelogs/{id} returns 200 OK and the correct JSON body.
     */
    @Test
    void getDiveLogById_ShouldReturnDiveLog() throws Exception {
        // Arrange
        Long logId = 1L;
        when(diveLogService.getDiveLogById(logId)).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/divelogs/{id}", logId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(logId))
                .andExpect(jsonPath("$.location").value("Blue Hole"));
    }

    /**
     * Tests GET /api/divelogs/{id} returns 404 Not Found when the log does not exist.
     * <p>
     * Verifies that the GlobalExceptionHandler #handleNotFound correctly
     * intercepts the {@link DiveLogNotFoundException} and returns the formatted error message.
     */
    @Test
    void getDiveLogById_ShouldReturnNotFound_WhenLogDoesNotExist() throws Exception {
        // Arrange
        Long nonExistentId = 99L;
        when(diveLogService.getDiveLogById(nonExistentId))
                .thenThrow(new DiveLogNotFoundException(nonExistentId));

        // Act & Assert
        mockMvc.perform(get("/api/divelogs/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Dive log with id 99 not found"));
    }

    /**
     * Tests DELETE /api/divelogs/{id} returns 204 No Content.
     */
    @Test
    void deleteDiveLogById_ShouldReturnNoContent() throws Exception {
        // Arrange
        Long logId = 1L;
        doNothing().when(diveLogService).deleteDiveLog(logId);

        // Act & Assert
        mockMvc.perform(delete("/api/divelogs/{id}", logId))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    /**
     * Tests DELETE /api/divelogs/{id} returns 403 Forbidden when the user is not the owner.
     * <p>
     * Verifies that GlobalExceptionHandler #handleUnauthorizedResourceAccess
     * correctly catches the exception and returns a 403 status.
     */
    @Test
    void deleteDiveLog_ShouldReturnForbidden_WhenUserIsNotOwner() throws Exception {
        // Arrange
        Long logId = 1L;
        doThrow(new UnauthorizedResourceAccessException())
                .when(diveLogService).deleteDiveLog(logId);

        // Act & Assert
        mockMvc.perform(delete("/api/divelogs/{id}", logId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                .value("User not allowed to access this resource."));
    }

    /**
     * Tests PUT /api/divelogs/{id} returns 200 OK.
     */
    @Test
    void updateDiveLog_ShouldReturnUpdatedDiveLog() throws Exception {
        // Arrange
        Long logId = 1L;
        when(diveLogService.updateDiveLog(eq(logId), any(DiveLogUpdateRequestDTO.class)))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/divelogs/{id}", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Blue Hole"));
    }

    /**
     * Tests POST /api/divelogs/{id}/image returns 200 OK and the updated DTO.
     * Verifies handling of multipart/form-data.
     */
    @Test
    void uploadDiveLogImage_ShouldReturnUpdatedDiveLog() throws Exception {
        // Arrange
        Long logId = 1L;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "dive-site.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        when(diveLogService.uploadImage(eq(logId), any())).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/divelogs/{id}/image", logId)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(logId))
                .andExpect(jsonPath("$.location").value("Blue Hole"));
    }

    /**
     * Tests GET /api/divelogs/{id}/image returns 200 OK and the image bytes.
     */
    @Test
    void getDiveLogImage_ShouldReturnBytes() throws Exception {
        // Arrange
        Long logId = 1L;
        byte[] imageBytes = "fake-image-content".getBytes();
        when(diveLogService.getDiveLogImageBytes(logId)).thenReturn(imageBytes);

        // Act & Assert
        mockMvc.perform(get("/api/divelogs/{id}/image", logId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(imageBytes));
    }

    /**
     * Tests GET /api/divelogs/{id}/image returns 404 Not Found when the service returns null.
     */
    @Test
    void getDiveLogImage_ShouldReturnNotFoundWhenNull() throws Exception {
        // Arrange
        Long logId = 1L;
        when(diveLogService.getDiveLogImageBytes(logId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/divelogs/{id}/image", logId))
                .andExpect(status().isNotFound());
    }
}

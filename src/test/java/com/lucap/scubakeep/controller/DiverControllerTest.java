package com.lucap.scubakeep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucap.scubakeep.dto.DiverResponseDTO;
import com.lucap.scubakeep.dto.DiverUpdateRequestDTO;
import com.lucap.scubakeep.entity.Certification;
import com.lucap.scubakeep.entity.Role;
import com.lucap.scubakeep.service.DiverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // New Import
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiverController.class)
@AutoConfigureMockMvc(addFilters = false)
class DiverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert DTO to JSON string

    @MockitoBean // Replaces @MockBean
    private DiverService diverService;

    private DiverResponseDTO responseDTO;
    private DiverUpdateRequestDTO updateDTO;
    private UUID diverId;

    @BeforeEach
    void setUp() {
        diverId = UUID.randomUUID();

        responseDTO = DiverResponseDTO.builder()
                .id(diverId)
                .username("scubadiver")
                .role(Role.USER)
                .build();

        updateDTO = new DiverUpdateRequestDTO();
        updateDTO.setFirstName("Leonardo");
        updateDTO.setLastName("Vinci");
        updateDTO.setCountryCode("IT");
        updateDTO.setHighestCertification(Certification.UNCERTIFIED);
        updateDTO.setSpecialties(Set.of("Deep Diver", "Nitrox"));
    }

    /**
     * Tests GET /api/divers returns 200 OK and a list of divers.
     */
    @Test
    void getAllDivers_ShouldReturnList() throws Exception {
        when(diverService.getAllDivers()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/divers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("scubadiver"))
                .andExpect(jsonPath("$[0].id").value(diverId.toString()));
    }

    /**
     * Tests GET /api/divers/{id} returns 200 OK for a valid ID.
     */
    @Test
    void getDiverById_ShouldReturnDiver() throws Exception {
        when(diverService.getDiverById(diverId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/divers/{id}", diverId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("scubadiver"));
    }

    /**
     * Tests DELETE /api/divers/{id} returns 204 No Content.
     */
    @Test
    void deleteDiver_ShouldReturnNoContent() throws Exception {
        doNothing().when(diverService).deleteDiver(diverId);

        mockMvc.perform(delete("/api/divers/{id}", diverId))
                .andExpect(status().isNoContent());
    }

    /**
     * Tests PUT /api/divers/{id} with valid JSON body.
     */
    @Test
    void updateDiver_ShouldReturnUpdatedDiver() throws Exception {
        when(diverService.updateDiver(eq(diverId), any(DiverUpdateRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/divers/{id}", diverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(diverId.toString()));
    }

    /**
     * Tests POST /api/divers/{id}/image returns 200 OK and updated DTO.
     */
    @Test
    void uploadProfilePicture_ShouldReturnUpdatedDiver() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        when(diverService.uploadProfilePicture(eq(diverId), any()))
                .thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/divers/{id}/image", diverId)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(diverId.toString()));
    }

    /**
     * Tests GET /api/divers/{id}/image returns 200 OK and byte content.
     */
    @Test
    void getProfilePicture_ShouldReturnBytes() throws Exception {
        // Arrange
        byte[] imageBytes = "fake-image-bytes".getBytes();
        when(diverService.getProfilePictureBytes(diverId)).thenReturn(imageBytes);

        // Act & Assert
        mockMvc.perform(get("/api/divers/{id}/image", diverId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(imageBytes));
    }

    /**
     * Tests GET /api/divers/{id}/image returns 404 when no image exists.
     */
    @Test
    void getProfilePicture_ShouldReturnNotFound_WhenNoImage() throws Exception {
        // Arrange
        when(diverService.getProfilePictureBytes(diverId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/divers/{id}/image", diverId))
                .andExpect(status().isNotFound());
    }
}
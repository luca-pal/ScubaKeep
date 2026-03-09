package com.lucap.scubakeep.storage;

import com.lucap.scubakeep.exception.StorageOperationException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MinioStorageServiceTest {

    private MinioClient minioClient;
    private MinioStorageService storageService;
    private final String bucketName = "scuba-bucket";

    @BeforeEach
    void setUp() {
        minioClient = mock(MinioClient.class);
        storageService = new MinioStorageService(minioClient, bucketName);
    }

    /**
     * Tests that upload calls the minioClient successfully.
     */
    @Test
    void upload_ShouldCallMinioClient_WhenSuccessful() throws Exception {
        // Arrange
        String key = "test-image.jpg";
        InputStream stream = new ByteArrayInputStream("content".getBytes());

        // Act & Assert
        assertDoesNotThrow(() ->
                storageService.upload(key, stream, 7, "image/jpeg")
        );
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    /**
     * Tests that upload throws StorageOperationException when the client fails.
     * Exercises the 'catch' block and the custom exception constructor.
     */
    @Test
    void upload_ShouldThrowStorageException_WhenMinioFails() throws Exception {
        // Arrange
        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException("Connection error"));

        // Act & Assert
        StorageOperationException ex = assertThrows(StorageOperationException.class, () ->
                storageService.upload("key", null, 0, "type")
        );
        assertEquals("Object storage operation failed for key: key", ex.getMessage());
    }

    /**
     * Tests that download returns the correct byte array.
     * <p>
     * Verifies that the {@link MinioStorageService#download} correctly
     * transfers data from the MinIO stream to a byte array.
     */
    @Test
    void download_ShouldReturnBytes_WhenSuccessful() throws Exception {
        // Arrange
        String key = "test.jpg";
        byte[] expectedContent = "file-data".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(expectedContent);
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);

        when(mockResponse.transferTo(any())).thenAnswer(invocation -> {
            return bais.transferTo(invocation.getArgument(0));
        });

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        // Act
        byte[] actualContent = storageService.download(key);

        // Assert
        assertArrayEquals(expectedContent, actualContent);
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    /**
     * Tests that download throws StorageOperationException on failure.
     */
    @Test
    void download_ShouldThrowStorageException_WhenMinioFails() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("Read error"));

        assertThrows(StorageOperationException.class, () -> storageService.download("key"));
    }
}

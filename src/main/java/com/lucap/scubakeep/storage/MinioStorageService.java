package com.lucap.scubakeep.storage;

import com.lucap.scubakeep.exception.StorageOperationException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Service responsible for storing and retrieving objects from MinIO.
 */
@Service
public class MinioStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioStorageService(
            MinioClient minioClient,
            @Value("${storage.minio.bucket}") String bucketName
    ) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    /**
     * Uploads an object to MinIO under the given key.
     *
     * @param objectKey the key (path) under which the object will be stored
     * @param inputStream the object content
     * @param size the content length in bytes
     * @param contentType the MIME type (e.g. image/png)
     */
    public void upload(
            String objectKey,
            InputStream inputStream,
            long size,
            String contentType
    ) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageOperationException(objectKey);
        }
    }

    /**
     * Downloads an object from MinIO.
     *
     * @param objectKey the key (path) of the object to download
     * @return the object content as bytes
     */
    public byte[] download(String objectKey) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectKey)
                        .build());
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        ) {
            inputStream.transferTo(buffer);
            return buffer.toByteArray();
        } catch (Exception ex) {
            throw new StorageOperationException(objectKey);
        }
    }
}
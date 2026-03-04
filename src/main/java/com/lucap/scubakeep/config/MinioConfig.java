package com.lucap.scubakeep.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for creating and configuring the {@link MinioClient}
 * used to interact with the MinIO object storage service.
 *
 * <p>The connection parameters (endpoint URL, access key, and secret key) are
 * injected from the application configuration and environment variables</p>
 */
@Configuration
public class MinioConfig {


    /**
     * Creates and configures a {@link MinioClient} bean used for accessing the MinIO
     * object storage service.
     *
     * @param url the MinIO server endpoint URL
     * @param accessKey the access key used for authentication
     * @param secretKey the secret key used for authentication
     * @return a configured {@link MinioClient} instance
     */
    @Bean
    public MinioClient minioClient(
            @Value("${storage.minio.url}") String url,
            @Value("${storage.minio.access-key}") String accessKey,
            @Value("${storage.minio.secret-key}") String secretKey
    ) {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
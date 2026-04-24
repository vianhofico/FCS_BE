package com.fcs.be.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app.storage.minio", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MinioConfig {

    @Bean
    public MinioClient minioClient(AppConfigHelper appConfigHelper) {
        return MinioClient.builder()
            .endpoint(appConfigHelper.minioEndpoint())
            .credentials(appConfigHelper.minioAccessKey(), appConfigHelper.minioSecretKey())
            .build();
    }

    @Bean
    public MinioBucketInitializer minioBucketInitializer(
        MinioClient minioClient,
        AppConfigHelper appConfigHelper
    ) {
        return new MinioBucketInitializer(minioClient, appConfigHelper);
    }

    public static class MinioBucketInitializer {
        public MinioBucketInitializer(MinioClient minioClient, AppConfigHelper appConfigHelper) {
            try {
                boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(appConfigHelper.minioBucket()).build()
                );
                if (!exists) {
                    minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(appConfigHelper.minioBucket()).build()
                    );
                }
            } catch (Exception ex) {
                throw new IllegalStateException("Cannot initialize MinIO bucket", ex);
            }
        }
    }
}

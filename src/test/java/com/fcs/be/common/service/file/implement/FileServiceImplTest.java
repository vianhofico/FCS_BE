package com.fcs.be.common.service.file.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fcs.be.common.service.file.PresignedUrlResult;
import com.fcs.be.common.service.file.interfaces.FileService;
import com.fcs.be.config.AppConfigHelper;
import com.fcs.be.config.AppCorsProperties;
import com.fcs.be.config.AppSecurityProperties;
import com.fcs.be.config.MinioProperties;
import io.minio.MinioClient;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FileServiceImplTest {

    private final MinioClient minioClient = Mockito.mock(MinioClient.class);
    private final MinioProperties minioProperties = new MinioProperties(
        "http://localhost:9000",
        "minioadmin",
        "minioadmin",
        "fcs-media",
        false,
        604800L
    );
    private final AppConfigHelper appConfigHelper = new AppConfigHelper(
        new AppCorsProperties(List.of("http://localhost:3000")),
        new AppSecurityProperties("secret", "access", "refresh", 900L, 2592000L, "fcs-be"),
        minioProperties
    );
    private final FileService fileService = new FileServiceImpl(minioClient, appConfigHelper);

    @Test
    void shouldGenerateUploadPresignedUrl() throws Exception {
        when(minioClient.getPresignedObjectUrl(any())).thenReturn("http://localhost:9000/upload-url");

        PresignedUrlResult result = fileService.generateUploadUrl("media/object-1.png", Duration.ofMinutes(10));

        assertEquals("media/object-1.png", result.objectKey());
        assertEquals("http://localhost:9000/upload-url", result.url());
        assertTrue(result.expiresAt().isAfter(java.time.Instant.now()));
    }

    @Test
    void shouldThrowWhenExpiryExceedsMaxAllowed() {
        assertThrows(
            IllegalArgumentException.class,
            () -> fileService.generateDownloadUrl("media/object-1.png", Duration.ofDays(8))
        );
    }
}

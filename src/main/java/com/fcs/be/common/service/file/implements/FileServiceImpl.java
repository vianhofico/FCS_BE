package com.fcs.be.common.service.file.implements;

import com.fcs.be.common.service.file.PresignedUrlResult;
import com.fcs.be.common.service.file.interfaces.FileService;
import com.fcs.be.config.AppConfigHelper;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@ConditionalOnBean(MinioClient.class)
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final AppConfigHelper appConfigHelper;

    public FileServiceImpl(
        MinioClient minioClient,
        AppConfigHelper appConfigHelper
    ) {
        this.minioClient = minioClient;
        this.appConfigHelper = appConfigHelper;
    }

    @Override
    public PresignedUrlResult generateUploadUrl(String objectKey, Duration expiry) {
        return generatePresignedUrl(objectKey, expiry, Http.Method.PUT);
    }

    @Override
    public PresignedUrlResult generateDownloadUrl(String objectKey, Duration expiry) {
        return generatePresignedUrl(objectKey, expiry, Http.Method.GET);
    }

    private PresignedUrlResult generatePresignedUrl(
        String objectKey,
        Duration expiry,
        Http.Method method
    ) {
        Assert.hasText(objectKey, "objectKey must not be blank");
        Assert.notNull(expiry, "expiry must not be null");
        Assert.isTrue(!expiry.isNegative() && !expiry.isZero(), "expiry must be greater than zero");
        Duration maxExpiry = Duration.ofSeconds(appConfigHelper.minioPresignedMaxExpirySeconds());
        Assert.isTrue(
            expiry.compareTo(maxExpiry) <= 0,
            "expiry must be less than or equal to configured max value"
        );

        int expiresInSeconds = Math.toIntExact(expiry.toSeconds());
        try {
            String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(method)
                    .bucket(appConfigHelper.minioBucket())
                    .object(objectKey)
                    .expiry(expiresInSeconds, TimeUnit.SECONDS)
                    .build()
            );
            return new PresignedUrlResult(objectKey, url, Instant.now().plusSeconds(expiresInSeconds));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate pre-signed URL for object: " + objectKey, ex);
        }
    }
}

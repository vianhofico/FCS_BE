package com.fcs.be.config;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AppConfigHelper {

    private final AppCorsProperties corsProperties;
    private final AppSecurityProperties securityProperties;
    private final MinioProperties minioProperties;

    public AppConfigHelper(
        AppCorsProperties corsProperties,
        AppSecurityProperties securityProperties,
        MinioProperties minioProperties
    ) {
        this.corsProperties = corsProperties;
        this.securityProperties = securityProperties;
        this.minioProperties = minioProperties;
    }

    public List<String> allowedOrigins() {
        return corsProperties.allowedOrigins();
    }

    public String jwtSecret() {
        return securityProperties.jwtSecret();
    }

    public String jwtAccessSecret() {
        return securityProperties.jwtAccessSecret();
    }

    public String jwtRefreshSecret() {
        return securityProperties.jwtRefreshSecret();
    }

    public long jwtAccessTtlSeconds() {
        return securityProperties.jwtAccessTtlSeconds();
    }

    public long jwtRefreshTtlSeconds() {
        return securityProperties.jwtRefreshTtlSeconds();
    }

    public String jwtIssuer() {
        return securityProperties.jwtIssuer();
    }

    public String minioBucket() {
        return minioProperties.bucket();
    }

    public String minioEndpoint() {
        return minioProperties.endpoint();
    }

    public String minioAccessKey() {
        return minioProperties.accessKey();
    }

    public String minioSecretKey() {
        return minioProperties.secretKey();
    }

    public boolean minioSecure() {
        return minioProperties.secure();
    }

    public long minioPresignedMaxExpirySeconds() {
        return minioProperties.presignedMaxExpirySeconds();
    }
}

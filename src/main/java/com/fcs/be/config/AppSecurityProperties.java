package com.fcs.be.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProperties(
    String jwtSecret,
    String jwtAccessSecret,
    String jwtRefreshSecret,
    long jwtAccessTtlSeconds,
    long jwtRefreshTtlSeconds,
    String jwtIssuer
) {
}

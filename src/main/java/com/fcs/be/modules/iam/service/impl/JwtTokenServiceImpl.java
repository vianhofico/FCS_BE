package com.fcs.be.modules.iam.service.impl;

import com.fcs.be.config.AppConfigHelper;
import com.fcs.be.modules.iam.service.interfaces.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private final AppConfigHelper appConfigHelper;

    public JwtTokenServiceImpl(AppConfigHelper appConfigHelper) {
        this.appConfigHelper = appConfigHelper;
    }

    @Override
    public String generateAccessToken(UUID userId, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(appConfigHelper.jwtAccessTtlSeconds());
        return Jwts.builder()
            .claims(claims)
            .subject(userId.toString())
            .issuer(appConfigHelper.jwtIssuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(accessKey())
            .compact();
    }

    @Override
    public String generateRefreshToken(UUID userId, Map<String, Object> claims) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(appConfigHelper.jwtRefreshTtlSeconds());
        return Jwts.builder()
            .claims(claims)
            .subject(userId.toString())
            .issuer(appConfigHelper.jwtIssuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(refreshKey())
            .compact();
    }

    @Override
    public Claims parseAccessToken(String token) {
        return Jwts.parser()
            .verifyWith(accessKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    @Override
    public Claims parseRefreshToken(String token) {
        return Jwts.parser()
            .verifyWith(refreshKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey accessKey() {
        return Keys.hmacShaKeyFor(appConfigHelper.jwtAccessSecret().getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey refreshKey() {
        return Keys.hmacShaKeyFor(appConfigHelper.jwtRefreshSecret().getBytes(StandardCharsets.UTF_8));
    }
}

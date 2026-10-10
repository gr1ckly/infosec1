package ru.itmo.secureapi.security;

import java.time.Duration;
import java.util.Base64;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.security")
public record SecurityProperties(
        String jwtSecret,
        String issuer,
        String audience,
        Duration accessTokenTtl) {

    private static final int MINIMUM_HS256_KEY_BYTES = 32;

    public SecurityProperties {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalArgumentException("JWT secret must be configured");
        }
        byte[] decodedSecret;
        try {
            decodedSecret = Base64.getDecoder().decode(jwtSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("JWT secret must be valid Base64", exception);
        }
        if (decodedSecret.length < MINIMUM_HS256_KEY_BYTES) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("JWT issuer must be configured");
        }
        if (audience == null || audience.isBlank()) {
            throw new IllegalArgumentException("JWT audience must be configured");
        }
        if (accessTokenTtl == null || accessTokenTtl.isZero() || accessTokenTtl.isNegative()) {
            throw new IllegalArgumentException("JWT access token TTL must be positive");
        }
    }

    public byte[] decodedSecret() {
        return Base64.getDecoder().decode(jwtSecret);
    }
}

package com.omkar.hireflow.service;

import com.omkar.hireflow.entity.Role;
import com.omkar.hireflow.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final long TOKEN_LIFETIME_SECONDS = 8 * 60 * 60;

    private final byte[] secret;

    public TokenService(@Value("${hireflow.auth.secret:change-this-hireflow-secret-in-production}") String secret) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String createToken(Long userId, Role role) {
        long expiresAt = Instant.now().plusSeconds(TOKEN_LIFETIME_SECONDS).getEpochSecond();
        String payload = userId + "|" + role.name() + "|" + expiresAt;
        String encodedPayload = encode(payload);
        return encodedPayload + "." + sign(encodedPayload);
    }

    public TokenData parseToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authentication token is required");
        }

        String token = authorizationHeader.substring(7).trim();
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        String payload = parts[0];
        String expectedSignature = sign(payload);
        if (!constantTimeEquals(expectedSignature, parts[1])) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        try {
            String[] values = decode(payload).split("\\|");
            if (values.length != 3) {
                throw new UnauthorizedException("Invalid authentication token");
            }

            Long userId = Long.valueOf(values[0]);
            Role role = Role.valueOf(values[1]);
            long expiresAt = Long.parseLong(values[2]);

            if (Instant.now().getEpochSecond() >= expiresAt) {
                throw new UnauthorizedException("Authentication token has expired");
            }

            return new TokenData(userId, role, expiresAt);
        } catch (UnauthorizedException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid authentication token");
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign authentication token", ex);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private boolean constantTimeEquals(String a, String b) {
        return java.security.MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }

    public record TokenData(Long userId, Role role, long expiresAt) {
    }
}

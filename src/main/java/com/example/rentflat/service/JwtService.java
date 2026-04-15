package com.example.rentflat.service;

import com.example.rentflat.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final StringRedisTemplate redis;

    // ── Token Generation ───────────────────────────────────────────────────────

    public String generateAccessToken(String phone, UserRole role, UUID userId) {
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .subject(phone)
                .claim("role", role.name())
                .claim("userId", userId.toString())
                .id(jti)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey())
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        String token = UUID.randomUUID().toString();
        redis.opsForValue().set(refreshKey(userId), token, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        return token;
    }

    // ── Token Validation ───────────────────────────────────────────────────────

    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (isBlacklisted(claims.getId())) return false;
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(UUID userId, String token) {
        String stored = redis.opsForValue().get(refreshKey(userId));
        return token.equals(stored);
    }

    // ── Claims Extraction ──────────────────────────────────────────────────────

    public String extractPhone(String token)  { return extractAllClaims(token).getSubject(); }
    public String extractRole(String token)   { return extractAllClaims(token).get("role", String.class); }
    public String extractUserId(String token) { return extractAllClaims(token).get("userId", String.class); }
    public String extractJti(String token)    { return extractAllClaims(token).getId(); }

    // ── Logout / Blacklist ─────────────────────────────────────────────────────

    public void blacklistAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redis.opsForValue().set(blacklistKey(claims.getId()), "1", ttl, TimeUnit.MILLISECONDS);
            }
        } catch (JwtException ignored) {
            // Already invalid — nothing to blacklist
        }
    }

    public void deleteRefreshToken(UUID userId) {
        redis.delete(refreshKey(userId));
    }

    // ── Internals ──────────────────────────────────────────────────────────────

    private boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey(blacklistKey(jti)));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private static String refreshKey(UUID userId)  { return "refresh:" + userId; }
    private static String blacklistKey(String jti) { return "jwt:blacklist:" + jti; }
}

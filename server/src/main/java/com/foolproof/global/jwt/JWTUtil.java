package com.foolproof.global.jwt;

import static java.util.Collections.rotate;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final HashMap<String, Long> expiredMsMap = new HashMap<>();

    public JWTUtil(@Value("${SPRING_JWT_SECRET}") String secretKey) {
        this.secretKey = new SecretKeySpec(
            secretKey.getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256
                .key()
                .build()
                .getAlgorithm()
        );
        this.expiredMsMap.put("access", 60 * 10 * 1000L);
        this.expiredMsMap.put("refresh", 24 * 60 * 60 * 1000L);
    }

    public String getCategory(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("category", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration()
            .before(new Date());
    }

    public String createJwt(String category, String username, String role) {
        Long curTimeMils = System.currentTimeMillis();
        Long expiredMs = expiredMsMap.get(category);
        Date curTime = new Date(curTimeMils);
        Date expTime = new Date(curTimeMils + expiredMs);
        return Jwts.builder()
            .claim("category", category)
            .claim("username", username)
            .claim("role", role)
            .issuedAt(curTime)
            .expiration(expTime)
            .signWith(secretKey)
            .compact();
    }
}

package com.foolproof.global.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final HashMap<String, Long> expiredMsMap = new HashMap<>();

    public JwtUtil(@Value("${SPRING_JWT_SECRET}") String secretKey) {
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
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
        } catch (JwtException e) {
            // Change logging method later.
            System.out.println("Jwt is invalid: " + e.getMessage());
            return null;
        }
    }

    public String getUsername(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
        } catch (JwtException e) {
            // Change logging method later.
            System.out.println("Jwt is invalid: " + e.getMessage());
            return null;
        }
    }

    public String getRole(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        } catch (JwtException e) {
            // Change logging method later.
            System.out.println("Jwt is invalid: " + e.getMessage());
            return null;
        }
    }

    public Boolean isExpired(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                // Throws ExpiredJwtException if the token is expired.
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
        } catch (ExpiredJwtException e) {
            // Change logging method later.
            System.out.println("Jwt has expired: " + e.getMessage());
        } catch (JwtException e) {
            // Change logging method later.
            System.out.println("Jwt is invalid: " + e.getMessage());
        }
        return false;
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

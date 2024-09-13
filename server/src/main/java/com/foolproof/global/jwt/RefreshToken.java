package com.foolproof.global.jwt;

import jakarta.persistence.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "refresh", timeToLive = 60 * 60 * 24) // Set TTL of refresh token to 1 day.
public class RefreshToken {

    @Id
    private String refresh;
    private String username;

    @Builder
    public RefreshToken(String refresh, String username) {
        this.refresh = refresh;
        this.username = username;
    }
}

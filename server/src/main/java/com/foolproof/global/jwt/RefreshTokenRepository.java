package com.foolproof.global.jwt;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String>{
    Boolean existsByRefreshToken(String refresh);
    void deleteByRefreshToken(String refresh);
}

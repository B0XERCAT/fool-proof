package com.foolproof.domain.user.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.foolproof.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserJoinDTO {
    private String username;
    private String password;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        return User.builder()
                .username(username)
                .password(encryptedPassword)
                .role("ROLE_ADMIN")
                .build();
    }
}

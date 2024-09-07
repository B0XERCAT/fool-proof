package com.foolproof.domain.user.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.foolproof.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private String username;
    private String password;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        String encryptedPassword = bCryptPasswordEncoder.encode(password);
        return User.builder()
                .username(username)
                .password(encryptedPassword)
                .build();
    }
}

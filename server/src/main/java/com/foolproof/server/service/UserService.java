package com.foolproof.server.service;

import org.springframework.stereotype.Service;

import com.foolproof.server.domain.User;
import com.foolproof.server.dto.UserDTO;
import com.foolproof.server.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User save(UserDTO request) {
        return userRepository.save(request.toEntity());
    }
}

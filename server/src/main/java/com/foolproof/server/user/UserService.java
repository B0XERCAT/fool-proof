package com.foolproof.server.user;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User save(UserDTO request) {
        return userRepository.save(request.toEntity());
    }
}

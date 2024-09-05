package com.foolproof.domain.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foolproof.domain.user.UserRepository;
import com.foolproof.domain.user.dto.UserJoinDTO;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserJoinService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Boolean save(UserJoinDTO request) {

        Boolean userExists = userRepository.existsByUsername(request.getUsername());

        if (userExists) 
            return false;

        userRepository.save(request.toEntity(bCryptPasswordEncoder));
        
        return true;
    }
}

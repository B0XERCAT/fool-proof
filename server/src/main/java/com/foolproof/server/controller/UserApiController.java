package com.foolproof.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foolproof.server.domain.User;
import com.foolproof.server.dto.UserDTO;
import com.foolproof.server.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserApiController {
    private final UserService userService;

    @PostMapping("/test/user/add")
    public ResponseEntity<User> addUser(@RequestBody UserDTO request) {
        User savedUser = userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }
}

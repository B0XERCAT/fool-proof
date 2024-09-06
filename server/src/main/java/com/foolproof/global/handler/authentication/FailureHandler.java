package com.foolproof.global.handler.authentication;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import com.foolproof.global.jwt.JWTUtil;

import java.io.IOException;

@RequiredArgsConstructor
public class FailureHandler implements AuthenticationFailureHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException failure
    ) throws IOException {
        // Change Exeption raised by the class later on.

        // Write custom authentication failure logic down below.
        response.setStatus(401);
    }
}
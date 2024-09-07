package com.foolproof.global.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foolproof.domain.user.dto.UserDTO;
import com.foolproof.global.jwt.JWTUtil;
import com.foolproof.global.handler.authentication.FailureHandler;
import com.foolproof.global.handler.authentication.SuccessHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import java.io.IOException;


public class LoginFilter extends UsernamePasswordAuthenticationFilter{  

    public final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(new SuccessHandler(jwtUtil));
        setAuthenticationFailureHandler(new FailureHandler(jwtUtil));
    }
    
    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, 
        HttpServletResponse response
    ) throws AuthenticationException {
        UserDTO parsedUserData = parseUserData(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            parsedUserData.getUsername(),
            parsedUserData.getPassword(),
            null
        );

        return getAuthenticationManager().authenticate(authToken);
    }

    public UserDTO parseUserData(HttpServletRequest request) throws RuntimeException{
        try {
            return objectMapper.readValue(
                request.getInputStream(), 
                UserDTO.class
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse login request", e);
        }
    }
}

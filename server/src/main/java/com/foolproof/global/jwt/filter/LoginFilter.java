package com.foolproof.global.jwt.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foolproof.domain.user.dto.UserDTO;
import com.foolproof.global.jwt.JWTUtil;
import com.foolproof.global.jwt.RefreshTokenRepository;
import com.foolproof.global.handler.authentication.FailureHandler;
import com.foolproof.global.handler.authentication.SuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;


public class LoginFilter extends UsernamePasswordAuthenticationFilter{  

    public final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(
        AuthenticationManager authenticationManager, 
        JWTUtil jwtUtil, 
        RefreshTokenRepository refreshTokenRepository
    ) {
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(
            new SuccessHandler(
                jwtUtil, 
                refreshTokenRepository
            )
        );
        setAuthenticationFailureHandler(new FailureHandler());
    }
    
    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, 
        HttpServletResponse response
    ) throws AuthenticationException {
        // Parse raw JSON file.
        UserDTO parsedUserData = parseUserData(request);

        // Create authentication token to validate.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            parsedUserData.getUsername(),
            parsedUserData.getPassword(),
            null
        );

        // Authenticate.
        return getAuthenticationManager().authenticate(authToken);
    }

    public UserDTO parseUserData(HttpServletRequest request) throws RuntimeException{
        /* Returns an instance of UserDTO from raw JSON POST request from the frontend server.
         * If login request from the front server does not have required fields, throws
         * RuntimeExeption.
         * 
         * @param request       HttpServeletRequest instance from the frontend server 
         *                      require to be parsed.
         * @return              Returns an instance of UserDTO class with parsed username
         *                      and password information from input request.
         */
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

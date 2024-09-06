package com.foolproof.global.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return getAuthenticationManager().authenticate(authToken);
    }
}

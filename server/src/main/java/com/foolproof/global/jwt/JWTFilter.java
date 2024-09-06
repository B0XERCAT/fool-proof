package com.foolproof.global.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foolproof.domain.user.User;
import com.foolproof.domain.user.dto.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter{

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {
        // Find Authrization header
        String authorization = request.getHeader("Authorization");

        // Validate Authorization header
        if (authorization == null ||!authorization.startsWith("Bearer ")) {
            System.out.println("TOKEN NULL");
            filterChain.doFilter(request, response);
            return;
        }

        // Get JWT token
        String token = authorization.split(" ")[1];

        // Validate expiration time of JWT
        if (jwtUtil.isExpired(token)) {
            System.out.println("TOKEN EXPIRED");
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        CustomUserDetails userDetails = new CustomUserDetails(
            User.builder()
                .username(username)
                .password("placeholder")
                .role(role)
                .build()
        );

        Authentication authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null, 
            userDetails.getAuthorities()
        );

        SecurityContextHolder
            .getContext()
            .setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

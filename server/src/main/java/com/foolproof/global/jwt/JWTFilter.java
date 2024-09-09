package com.foolproof.global.jwt;

import java.io.IOException;
import java.io.PrintWriter;

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
        // Find access token from header.
        String accessToken = request.getHeader("access");

        // Verify non-empty access token
        if (accessToken == null) {
            // Add logging.

            // Go to next filter.
            filterChain.doFilter(request, response);
            return;
        }

        // Validate expiration time of JWT
        // Does not go to next filter when access token is expired. 
        if (jwtUtil.isExpired(accessToken)) {
            // Write to response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired.");

            // Raise response status
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Check if the token is access token.
        // Does not go to next filter when access token is not access. 
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {

            // Write to response body.
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");
        
            // Raise response status
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Create CustomUserDetails instance from information in jwt.
        CustomUserDetails userDetails = new CustomUserDetails(
            User.builder()
                .username(jwtUtil.getUsername(accessToken))
                .password("placeholder")
                .role(jwtUtil.getRole(accessToken))
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

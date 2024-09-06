package com.foolproof.global.handler.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import com.foolproof.domain.user.dto.CustomUserDetails;
import com.foolproof.global.jwt.JWTUtil;

import java.io.IOException;

@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        // Change Exeption raised by the class later on.

        // Write custom authentication success logic down below.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        // Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        // GrantedAuthority auth = iterator.next();
        // String role = auth.getAuthority();
        String role = authentication
            .getAuthorities()
            .iterator()
            .next()
            .getAuthority();

        String token = jwtUtil.createJwt(username, role);

        response.addHeader("Authorization", "Bearer " + token);
    }
}
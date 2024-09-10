package com.foolproof.global.handler.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import com.foolproof.global.jwt.JWTUtil;
import com.foolproof.global.jwt.RefreshToken;
import com.foolproof.global.jwt.RefreshTokenRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        // Change Exeption raised by the class later on.

        // Write custom authentication success logic down below.
        
        // Get username from authentication instance.
        String username = authentication.getName();
        
        // Get role of user from authentication instance
        String role = authentication
            .getAuthorities()  // Returns instance of `Collection<? extends GrantedAuthority>`
            .iterator()        // Returns instance of `Iterator<? extends GrantedAuthority>`
            .next()            // Returns instance of `GrantedAuthority`
            .getAuthority();

        // Generate JWTs
        String accessToken = jwtUtil.createJwt("access", username, role);
        String refreshToken = jwtUtil.createJwt("refresh", username, role);

        refreshTokenRepository.save(
            RefreshToken
                .builder()
                .refresh(refreshToken)
                .username(username)
                .build()
        );

        // Generate response
        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
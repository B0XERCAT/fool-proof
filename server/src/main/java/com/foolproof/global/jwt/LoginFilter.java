package com.foolproof.global.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.foolproof.domain.user.dto.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter{
    
    // Change when needed.
    private final Long EXPIREDMS = 60 * 60 * 10L;

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filter, Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        String username = userDetails.getUsername();

        // Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        // Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        // GrantedAuthority authentication = iterator.next();
        // String role = authentication.getAuthority();
        String role = auth
            .getAuthorities()
            .iterator()
            .next()
            .getAuthority();

        String token = jwtUtil.createJwt(username, role, EXPIREDMS);

        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }
}

package com.foolproof.global.jwt.filter;

import com.foolproof.global.jwt.JwtUtil;
import com.foolproof.global.jwt.RefreshTokenRepository;

import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain filterChain
    ) throws IOException, ServletException {
        // Change Servlet instances to HttpServlet instances
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Verify logout path
        String requestUri = httpRequest.getRequestURI();
        if (!requestUri.matches("^\\/logout")) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        // Verify that request method is POST
        String requestMethod = httpRequest.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(httpRequest, httpResponse);
            return;
        }

        // Get refresh token
        String refresh = getRefresh(httpRequest.getCookies());

        // Refresh null check
        if (refresh == null) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Token expiry check
        if (jwtUtil.isExpired(refresh)) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Check if token is refresh
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Check token existence
        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Proceed to logout.
        // Delete refresh token from redis db
        refreshTokenRepository.deleteByRefreshToken(refresh);

        // Set refresh token cookie to null
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        httpResponse.addCookie(cookie);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
    }
    
    private String getRefresh(Cookie[] cookies) {
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("refresh"))
                return cookie.getValue();
        }
        return null;
    }
}

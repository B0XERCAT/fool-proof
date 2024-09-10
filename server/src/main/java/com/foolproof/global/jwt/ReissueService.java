package com.foolproof.global.jwt;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ReissueStatus validateToken(String refresh) {
        if (refresh == null) {
            return ReissueStatus.TOKEN_NULL;
        }

        if (jwtUtil.isExpired(refresh)) {
            return ReissueStatus.TOKEN_EXPIRED;
        }

        if (!jwtUtil.getCategory(refresh).equals("refresh")) {
            return ReissueStatus.TOKEN_NOT_REFRESH;
        }
        if (!refreshTokenRepository.existsByRefreshToken(refresh)) {
            return ReissueStatus.TOKEN_ABSENT;
        }

        return ReissueStatus.TOKEN_VALID;
    }

    public Pair<String, Cookie> onSuccess(String refresh) {
        /*
         * From given token, create new access and refresh token.
         * Then, return access token and "cookie-fied" refresh token.
         * 
         * @param refresh String representation of old refresh token
         * 
         * @return        An instance of Pair of String and Cookie.
         *                An instance of string (the first in pair) is new access token.
         *                An instance of Cookie (the second in pair) is new refresh token.
         */
        // Parse username and password from refresh token
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // Create new access token
        String newAccessToken = getNewAccessToken(
            username,
            role
        );
        
        // Create new refresh token
        String newRefreshToken = getNewRefreshToken(
            username,
            role
        );

        // Create new cookie from new refresh token
        Cookie newRefreshTokenCookie = toCookie("refresh", newRefreshToken);

        // Delete old refresh token and add new one
        deleteByRefreshToken(refresh);
        addRefreshToken(newAccessToken);

        return Pair.of(
            newAccessToken,
            newRefreshTokenCookie
        );
    }

    public String getNewAccessToken(String refresh) {
        /* 
         * Get String representation of newly created access token
         * from already existing refresh token.
         * 
         * @param refresh String representation of old refresh token.
         * 
         * @return        String representation of new access token.
         */
        return jwtUtil.createJwt(
            "access",
            jwtUtil.getUsername(refresh),
            jwtUtil.getRole(refresh)
        );
    }

    public String getNewAccessToken(String username, String role) {
        /* 
         * Get String representation of newly created access token
         * from username and role.
         * 
         * @param username String representation of username.
         * @param role     String representation of role.
         * 
         * @return         String representation of new access token.
         */
        return jwtUtil.createJwt(
            "access", 
            username, 
            role
        );
    }

    public String getNewRefreshToken(String refresh) {
        /* 
         * Get String representation of newly created refresh token
         * from already existing refresh token.
         * 
         * @param refresh String representation of old refresh token.
         * 
         * @return        String representation of new refresh token.
         */
        return jwtUtil.createJwt(
            "refresh",
            jwtUtil.getUsername(refresh),
            jwtUtil.getRole(refresh)
        );
    }

    public String getNewRefreshToken(String username, String role) {
        /* 
         * Get String representation of newly created refresh token
         * from username and role.
         * 
         * @param username String representation of username.
         * @param role     String representation of role.
         * 
         * @return         String representation of new refresh token.
         */
        return jwtUtil.createJwt(
            "refresh",
            username,
            role
        );
    }

    public Cookie toCookie(String key, String value) {
        Cookie cookie = new Cookie(
            key, 
            value
        );
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
    
    public Object getNewRefreshToken(String username, String role, Boolean asCookie) {
        String token = getNewRefreshToken(username, role);
        if (asCookie){
            return toCookie("refresh", token);
        } else {
            return token;
        }
    }

    public void deleteByRefreshToken(String refresh) {
        refreshTokenRepository.deleteByRefreshToken(refresh);
    }

    public void addRefreshToken(String refresh) {
        refreshTokenRepository.save(
            RefreshToken
                .builder()
                .refresh(refresh)
                .username(jwtUtil.getUsername(refresh))
                .build()
        );
    }

    public String generateMessage(ReissueStatus status) {
        switch (status) {
            case TOKEN_VALID:
                return "";
            case TOKEN_NULL:
                return "Refresh token null.";
            case TOKEN_EXPIRED:
                return "Refresh token expired.";
            case TOKEN_NOT_REFRESH:
                return "Token not refresh.";
            case TOKEN_ABSENT:
                return "Token does not exist in Redis DB.";
            default:
                return "Unknown token invalidation.";
        }
    }

    public HttpStatus raiseHttpStatus(ReissueStatus status) {
        if (status == ReissueStatus.TOKEN_VALID) {
            return HttpStatus.OK;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }
}

package com.foolproof.global.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;

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

        return ReissueStatus.TOKEN_VALID;
    }

    public String getNewAccessToken(String refresh) {
        return jwtUtil.createJwt(
            "access",
            jwtUtil.getUsername(refresh),
            jwtUtil.getRole(refresh)
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

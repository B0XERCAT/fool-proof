package com.foolproof.global.jwt;

import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;
    
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refresh = getRefresh(request.getCookies());

        ReissueStatus status = reissueService.validateToken(refresh);

        String message = reissueService.generateMessage(status);
        HttpStatus httpStatus = reissueService.raiseHttpStatus(status);
        
        if (status == ReissueStatus.TOKEN_VALID) {
            response.addHeader(
                "access",
                reissueService.getNewAccessToken(refresh)
            );
        }
        
        return new ResponseEntity<>(
          message,
          httpStatus  
        );
    }
    

    private String getRefresh(Cookie[] cookies) {
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals("refresh"))
                return cookie.getValue();
        }
        return null;
    }
}

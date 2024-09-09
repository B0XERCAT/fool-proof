package com.foolproof.domain.user.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/admin")
public class AdminUserApiController {
    
    @GetMapping("/test")
    public String getTest() {
        String username = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();
        String role = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getAuthorities()
            .iterator()
            .next()
            .getAuthority();
        return "Test Admin Controller Method: " + username + " with role " + role;
    }
    
}

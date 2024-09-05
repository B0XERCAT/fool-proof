package com.foolproof.domain.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/api")
public class AuthenticationLoginController {
    
    @GetMapping("/login")
    public String userLogIn() {
        return new String();
    }
    
}

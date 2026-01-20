package com.example.studybuddy.users;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;

    public AuthController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/me")
    public User me(Authentication auth) {
        String email = auth.getName();
        return users.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

}

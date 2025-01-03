package com.ws.websocketdemo.controller.rest;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
class AuthController {

    private final String SECRET_KEY;

    public AuthController(@Value("${SECRET_KEY_WS}") String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    @PostMapping("/auth/token")
    public Map<String, String> generateToken(@RequestBody Map<String, String> userDetails) {
        String username = userDetails.get("username");

        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        // Create token
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token expires in 1 day
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}
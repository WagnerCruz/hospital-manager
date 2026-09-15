package com.raidstack.controllers;

import com.raidstack.security.JwtTokenUtil;
import com.raidstack.services.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/authentication")
public class AuthenticationController {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserServiceImpl userService;

    public AuthenticationController(JwtTokenUtil jwtTokenUtil, UserServiceImpl userService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String username, @RequestParam String password) {
        if (userService.validateUserCredentials(username, password)) {
            String token = jwtTokenUtil.generateToken(username);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Usuario ou senha inválidos");
        }
    }

}

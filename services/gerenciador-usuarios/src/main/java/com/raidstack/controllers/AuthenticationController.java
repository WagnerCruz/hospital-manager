package com.raidstack.controllers;

import com.raidstack.services.impl.AutenticacaoServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/authentication")
public class AuthenticationController {

    private final AutenticacaoServiceImpl autenticacaoService;

    public AuthenticationController(AutenticacaoServiceImpl autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        return autenticacaoService.authenticate(authentication);
    }

}

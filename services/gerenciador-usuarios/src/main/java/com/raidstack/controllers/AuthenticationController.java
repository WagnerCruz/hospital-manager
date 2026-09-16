package com.raidstack.controllers;

import com.raidstack.services.impl.AutenticacaoServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/authentication")
public class AuthenticationController {

    private final AutenticacaoServiceImpl autenticacaoService;

    public AuthenticationController(AutenticacaoServiceImpl autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public String authenticateUser(Authentication authentication) {
        return autenticacaoService.authenticate(authentication);
    }

}

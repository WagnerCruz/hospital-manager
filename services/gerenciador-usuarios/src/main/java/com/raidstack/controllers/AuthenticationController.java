package com.raidstack.controllers;

import com.raidstack.dtos.AutenticarUsuarioDTO;
import com.raidstack.services.impl.AutenticacaoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/authentication")
public class AuthenticationController {

    @Autowired
    private AutenticacaoServiceImpl autenticacaoService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String authenticateUser(@RequestBody AutenticarUsuarioDTO autenticar) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                autenticar.login(),
                                autenticar.senha()
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        return autenticacaoService.authenticate(authentication);
    }

}

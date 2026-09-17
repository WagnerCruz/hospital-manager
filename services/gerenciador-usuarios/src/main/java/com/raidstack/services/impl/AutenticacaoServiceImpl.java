package com.raidstack.services.impl;

import com.raidstack.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoServiceImpl {

    private final JwtService jwtServiceImpl;

    public AutenticacaoServiceImpl(JwtService jwtServiceImpl) {
        this.jwtServiceImpl = jwtServiceImpl;
    }


    public String authenticate(Authentication authentication) {
        return jwtServiceImpl.generateToken(authentication);
    }

}

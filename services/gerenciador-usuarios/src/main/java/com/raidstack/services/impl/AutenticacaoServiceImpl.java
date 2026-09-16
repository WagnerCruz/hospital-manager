package com.raidstack.services.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoServiceImpl {

    private final JwtServiceImpl jwtServiceImpl;

    public AutenticacaoServiceImpl(JwtServiceImpl jwtServiceImpl) {
        this.jwtServiceImpl = jwtServiceImpl;
    }


    public String authenticate(Authentication authentication) {
        return jwtServiceImpl.generateToken(authentication);
    }

}

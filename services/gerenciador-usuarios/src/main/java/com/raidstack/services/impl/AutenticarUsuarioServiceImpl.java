package com.raidstack.services.impl;

import com.raidstack.dtos.AutenticarUsuarioDTO;
import com.raidstack.security.JwtService;
import com.raidstack.services.IAutenticarUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AutenticarUsuarioServiceImpl implements IAutenticarUsuarioService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtServiceImpl;

    @Override
    public String autenticarUsuario(AutenticarUsuarioDTO usuario) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                usuario.login(),
                                usuario.senha()
                        )
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        return jwtServiceImpl.generateToken(authentication);
    }

}

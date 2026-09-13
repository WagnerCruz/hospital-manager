package com.raidstack.services.impl;

import com.raidstack.dtos.AutenticarUsuarioDTO;
import com.raidstack.entities.Usuario;
import com.raidstack.repositories.IUsuarioRepository;
import com.raidstack.services.IAutenticarUsuarioService;
import com.raidstack.services.exceptions.ResourceBadRequestException;
import com.raidstack.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AutenticarUsuarioServiceImpl implements IAutenticarUsuarioService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public void autenticarUsuario(AutenticarUsuarioDTO usuario) {
        Optional<Usuario> usuarioSalvo = this.usuarioRepository.findUsuarioByLogin(usuario.login());
        if (usuarioSalvo.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        if (!passwordEncoder.matches(usuario.senha(), usuarioSalvo.get().getSenha())) {
            throw new ResourceBadRequestException("Senha inválida");
        }
    }

}

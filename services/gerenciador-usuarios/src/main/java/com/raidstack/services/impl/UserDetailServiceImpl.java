package com.raidstack.services.impl;

import com.raidstack.entities.UsuarioAutenticado;
import com.raidstack.repositories.IUsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final Map<String, String> users = new HashMap<>();
    private final IUsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDetailServiceImpl(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return usuarioRepository.findUsuarioByLogin(username)
                .map(UsuarioAutenticado::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado no banco de dados"));

    }

    public boolean validateUserCredentials(String username, String password) {
        String encodedPassword = users.get(username);
        if (encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(password, encodedPassword);
    }
}

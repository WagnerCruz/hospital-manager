package com.raidstack.services.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final Map<String, String> users = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String encodedPassword = users.get(username);
        if (encodedPassword == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        return org.springframework.security.core.userdetails.User.withUsername(username)
                .password(encodedPassword)
                .authorities(new ArrayList<>())
                .build();
    }

    public boolean validateUserCredentials(String username, String password) {
        String encodedPassword = users.get(username);
        if (encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(password, encodedPassword);
    }
}

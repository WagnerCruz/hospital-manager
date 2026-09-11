package com.raidstack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
@EnableWebSecurity
public class SecurityConfig {

    @Value("classpath:app.pub")
    private RSAPublicKey publicKey;

    @Value("classpath:app.priv")
    private RSAPrivateKey privateKey;

    @Bean
    SecurityFilterChain


}

package com.raidstack.controllers;

import com.raidstack.dtos.AutenticarUsuarioDTO;
import com.raidstack.services.IAutenticarUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/autenticar")
public class AutenticarUsuarioController {

    @Autowired
    private IAutenticarUsuarioService autenticarUsuarioService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AutenticarUsuarioDTO usuario) {
        this.autenticarUsuarioService.autenticarUsuario(usuario);
        return ResponseEntity.noContent().build();
    }

}

package com.raidstack.controllers;

import com.raidstack.dtos.AtualizarUsuarioPerfilDTO;
import com.raidstack.dtos.AtualizarUsuarioSenhaDTO;
import com.raidstack.dtos.AtualizarUsuarioDTO;
import com.raidstack.dtos.AtualizarUsuarioExternoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.services.IAtualizarUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/atualizar")
public class AtualizarUsuarioController {

    @Autowired
    private IAtualizarUsuarioService atualizarUsuarioService;

    @PatchMapping
    public ResponseEntity<VisualizarUsuarioDTO> atualizarUsuario(@RequestBody @Valid AtualizarUsuarioDTO usuario) {
        return ResponseEntity.ok(this.atualizarUsuarioService.atualizarUsuario(usuario));
    }

    @PatchMapping("/senha")
    public ResponseEntity<VisualizarUsuarioDTO> atualizarSenhaUsuario(@RequestBody @Valid AtualizarUsuarioSenhaDTO usuario) {
        this.atualizarUsuarioService.atualizarUsuarioSenha(usuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin")
    public ResponseEntity<VisualizarUsuarioDTO> atualizarUsuarioExterno(@RequestBody @Valid AtualizarUsuarioExternoDTO usuario) {
        return ResponseEntity.ok(this.atualizarUsuarioService.atualizarUsuarioExterno(usuario));
    }

    @PatchMapping("/admin/perfil")
    public ResponseEntity<VisualizarUsuarioDTO> atualizarUsuarioExterno(@RequestBody @Valid AtualizarUsuarioPerfilDTO usuario) {
        return ResponseEntity.ok(this.atualizarUsuarioService.atualizarUsuarioPerfil(usuario));
    }



}

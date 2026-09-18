package com.raidstack.controllers;

import com.raidstack.dtos.AtualizarUsuarioDTO;
import com.raidstack.dtos.AtualizarUsuarioExternoDTO;
import com.raidstack.dtos.AtualizarUsuarioPerfilDTO;
import com.raidstack.dtos.AtualizarUsuarioSenhaDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.services.IAtualizarUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/atualizar/usuario")
public class AtualizarUsuarioController {

    @Autowired
    private IAtualizarUsuarioService atualizarUsuarioService;

    @PatchMapping
    @PreAuthorize("@validarAcessoProprioUsuarioSecurity.validar(#usuario.id(), authentication)")
    public ResponseEntity<VisualizarUsuarioDTO> atualizarUsuario(@RequestBody @Valid AtualizarUsuarioDTO usuario) {
        return ResponseEntity.ok(this.atualizarUsuarioService.atualizarUsuario(usuario));
    }

    @PatchMapping("/senha")
    @PreAuthorize("@validarAcessoProprioUsuarioSecurity.validar(#usuario.id(), authentication)")
    public ResponseEntity<VisualizarUsuarioDTO> atualizarSenhaUsuario(@RequestBody @Valid AtualizarUsuarioSenhaDTO usuario) {
        this.atualizarUsuarioService.atualizarUsuarioSenha(usuario);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin")
    @PreAuthorize("hasAuthority('ADMINISTRADOR_USUARIO_ATUALIZAR')")
    public ResponseEntity<VisualizarUsuarioDTO> atualizarUsuarioExterno(@RequestBody @Valid AtualizarUsuarioExternoDTO usuario) {
        return ResponseEntity.ok(this.atualizarUsuarioService.atualizarUsuarioExterno(usuario));
    }

    @PatchMapping("/admin/perfil")
    @PreAuthorize("hasAuthority('ADMINISTRADOR_USUARIO_GERENCIAR_PERFIL')")
    public ResponseEntity<VisualizarUsuarioDTO> atualizarUsuarioExterno(@RequestBody @Valid AtualizarUsuarioPerfilDTO usuario) {
        return ResponseEntity.ok(this.atualizarUsuarioService.atualizarUsuarioPerfil(usuario));
    }



}

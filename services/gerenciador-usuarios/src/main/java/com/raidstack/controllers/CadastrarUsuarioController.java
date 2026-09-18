package com.raidstack.controllers;

import com.raidstack.dtos.CadastrarUsuarioDTO;
import com.raidstack.dtos.CadastrarUsuarioExternoDTO;
import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.services.ICadastrarUsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cadastrar/usuario")
public class CadastrarUsuarioController {

    private final ICadastrarUsuarioService cadastrarUsuarioService;

    public CadastrarUsuarioController(ICadastrarUsuarioService cadastrarUsuarioService) {
        this.cadastrarUsuarioService = cadastrarUsuarioService;
    }

    @PostMapping
    public ResponseEntity<VisualizarUsuarioDTO> cadastrarUsuario(@RequestBody @Valid CadastrarUsuarioDTO usuario) {
        return ResponseEntity.ok(this.cadastrarUsuarioService.cadastrarUsuarioDTO(usuario));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('ADMINISTRADOR_USUARIO_CRIAR')")
    public ResponseEntity<VisualizarUsuarioDTO> cadastrarUsuarioExterno(@RequestBody @Valid CadastrarUsuarioExternoDTO usuario) {
        return ResponseEntity.ok(this.cadastrarUsuarioService.cadastrarUsuarioExternoDTO(usuario));
    }

}

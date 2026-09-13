package com.raidstack.controllers;

import com.raidstack.dtos.VisualizarUsuarioDTO;
import com.raidstack.services.IBuscarUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/visualizar")
public class VisualizarUsuarioController {

    @Autowired
    private IBuscarUsuarioService buscarUsuarioService;

    //// TODO: IMPLEMENTAR DEPOIS UMA BUSCA PELO PRÓPRIO USUÁRIO

    @GetMapping("/{id}")
    public ResponseEntity<VisualizarUsuarioDTO> buscarUsuarioPorID(@PathVariable String id) {
        return ResponseEntity.ok(this.buscarUsuarioService.buscarUsuarioDTO(UUID.fromString(id)));
    }

    @GetMapping
    public ResponseEntity<Page<VisualizarUsuarioDTO>> buscarUsuario(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(this.buscarUsuarioService.buscarUsuarios(page, size));
    }

}
